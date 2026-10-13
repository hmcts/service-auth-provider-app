"""Exercise the public scripts against a fake az executable; never contact Azure."""

import base64
import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

BIN = Path(__file__).resolve().parents[1]
ENVIRONMENTS = ("sandbox", "demo", "aat", "prod", "perftest", "ithc")
NAME = "microservicekey-deed-poll-api"

FAKE_AZ = r'''#!/usr/bin/env python3
import json
import os
from pathlib import Path
import re
import sys

args = sys.argv[1:]
operation = args[2]
vault = args[args.index("--vault-name") + 1]
path = Path(os.environ["FAKE_AZ_STATE"])
state = json.loads(path.read_text())
state.setdefault("calls", []).append({"operation": operation, "vault": vault, "args": args})
def save():
    path.write_text(json.dumps(state))
save()
if state.get("fail") == [operation, vault]:
    print("simulated Azure failure with sensitive-write-response", file=sys.stderr)
    sys.exit(1)
if operation in ("list", "list-deleted"):
    if state.get("malformed") == vault:
        print("not-json")
        sys.exit(0)
    assert args[args.index("--query") + 1] == "[].{name:name,enabled:attributes.enabled}"
    name = state.get("name", "microservicekey-deed-poll-api")
    if operation == "list" and state.get("appear") == vault:
        reads = sum(c["operation"] == "list" and c["vault"] == vault for c in state["calls"])
        if reads == 2:
            state["existing"][vault] = True
            save()
    if operation == "list-deleted":
        print(json.dumps([{"name": name}] if vault in state.get("deleted", []) else []))
    elif vault in state["existing"]:
        print(json.dumps([{"name": name, "enabled": state["existing"][vault]}]))
    else:
        print("[]")
elif operation == "set":
    assert "--value" not in args
    assert args[args.index("--output") + 1] == "none"
    assert args[args.index("--encoding") + 1] == "utf-8"
    secret = Path(args[args.index("--file") + 1])
    value = secret.read_text()
    assert re.fullmatch(r"[A-Z2-7]{16}", value)
    assert secret.stat().st_mode & 0o777 == 0o600
    state.setdefault("writes", []).append({"vault": vault, "value": value, "file": str(secret)})
    if not state.get("lose_write"):
        state["existing"][vault] = True
    save()
    # Even an unexpectedly chatty CLI must not leak into captured PR evidence.
    print("sensitive-write-response " + value)
else:
    sys.exit("Unexpected Azure command: " + operation)
'''


class SecretScriptsTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix="s2s scripts ")
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        fake = self.root / "az"
        fake.write_text(FAKE_AZ)
        fake.chmod(0o755)
        self.state_path = self.root / "state.json"
        self.state = {"existing": {"s2s-" + env: True for env in ENVIRONMENTS}}
        self.env = dict(os.environ, PATH=str(self.root) + os.pathsep + os.environ["PATH"],
                        FAKE_AZ_STATE=str(self.state_path), TMPDIR=str(self.root))

    def run_script(self, script="set-secret-in-all-vaults", service="deed-poll-api"):
        self.state_path.write_text(json.dumps(self.state))
        result = subprocess.run([str(BIN / script), service], cwd=self.root, env=self.env,
                                capture_output=True, text=True)
        self.state = json.loads(self.state_path.read_text())
        self.assertNotIn("sensitive-write-response", result.stdout + result.stderr)
        for write in self.state.get("writes", []):
            self.assertNotIn(write["value"], result.stdout + result.stderr)
            self.assertFalse(Path(write["file"]).exists())
        return result

    def test_existing_secrets_are_preserved_without_writes(self):
        result = self.run_script()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(result.stdout.count("Preserved existing"), 6)
        self.assertEqual(result.stdout.count("Found enabled"), 6)
        self.assertEqual(self.state.get("writes", []), [])
        self.assertTrue(all(c["operation"] == "list" for c in self.state["calls"]))

    def test_existing_name_with_different_case_is_never_rotated(self):
        self.state["name"] = NAME.upper()
        result = self.run_script()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(self.state.get("writes", []), [])

    def test_only_missing_prod_is_created_and_rerun_does_not_rotate(self):
        del self.state["existing"]["s2s-prod"]
        result = self.run_script()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual([w["vault"] for w in self.state["writes"]], ["s2s-prod"])
        self.assertEqual(len(base64.b32decode(self.state["writes"][0]["value"])), 10)
        self.assertEqual(result.stdout.count("Found enabled"), 6)
        first_write = next(i for i, c in enumerate(self.state["calls"]) if c["operation"] == "set")
        self.assertEqual({c["vault"] for c in self.state["calls"][:first_write]},
                         {"s2s-" + env for env in ENVIRONMENTS})
        result = self.run_script()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(len(self.state["writes"]), 1)

    def test_first_registration_creates_independent_base32_values(self):
        self.state["existing"] = {}
        result = self.run_script()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(len(self.state["writes"]), 6)
        self.assertEqual(len({w["value"] for w in self.state["writes"]}), 6)

    def test_access_failure_in_late_vault_prevents_all_writes(self):
        del self.state["existing"]["s2s-sandbox"]
        self.state["fail"] = ["list", "s2s-ithc"]
        result = self.run_script()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("list failed for s2s-ithc", result.stderr)
        self.assertEqual(self.state.get("writes", []), [])

    def test_disabled_or_unknown_status_prevents_all_writes(self):
        for enabled in (False, None, "true"):
            with self.subTest(enabled=enabled):
                self.state["existing"]["s2s-prod"] = enabled
                result = self.run_script()
                self.assertNotEqual(result.returncode, 0)
                self.assertIn("disabled or has unknown", result.stderr)
                self.assertEqual(self.state.get("writes", []), [])

    def test_soft_deleted_name_is_not_recreated_or_purged(self):
        del self.state["existing"]["s2s-prod"]
        self.state["deleted"] = ["s2s-prod"]
        result = self.run_script()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("soft-deleted", result.stderr)
        self.assertEqual(self.state.get("writes", []), [])

    def test_deleted_metadata_access_failure_is_not_absence(self):
        del self.state["existing"]["s2s-prod"]
        self.state["fail"] = ["list-deleted", "s2s-prod"]
        result = self.run_script()
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(self.state.get("writes", []), [])

    def test_malformed_metadata_prevents_writes(self):
        self.state["malformed"] = "s2s-prod"
        result = self.run_script()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("Invalid metadata", result.stderr)
        self.assertEqual(self.state.get("writes", []), [])

    def test_write_failure_stops_without_retrying_or_exposing_response(self):
        self.state["existing"] = {}
        self.state["fail"] = ["set", "s2s-sandbox"]
        result = self.run_script()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("set failed", result.stderr)
        self.assertEqual(sum(c["operation"] == "set" for c in self.state["calls"]), 1)
        self.assertEqual(list(self.root.glob("s2s-secret-*")), [])

    def test_failed_post_write_verification_is_not_success(self):
        del self.state["existing"]["s2s-prod"]
        self.state["lose_write"] = True
        result = self.run_script()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("after creation", result.stderr)

    def test_secret_appearing_after_preflight_is_preserved(self):
        del self.state["existing"]["s2s-prod"]
        self.state["appear"] = "s2s-prod"
        result = self.run_script()
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("Preserved newly discovered", result.stdout)
        self.assertEqual(self.state.get("writes", []), [])

    def test_checker_fails_on_missing_secret_and_never_writes(self):
        del self.state["existing"]["s2s-prod"]
        result = self.run_script("check-all-vaults-for-s2s-secret")
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("missing from s2s-prod", result.stderr)
        self.assertTrue(all(c["operation"] == "list" for c in self.state["calls"]))

    def test_checker_succeeds_only_when_all_six_are_enabled(self):
        result = self.run_script("check-all-vaults-for-s2s-secret")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(result.stdout.count("Found enabled"), 6)

    def test_invalid_service_name_never_reaches_azure(self):
        for name in ("", "deed_poll_api", "bad'name", "x" * 113):
            with self.subTest(name=name):
                result = self.run_script(service=name)
                self.assertNotEqual(result.returncode, 0)
                self.assertEqual(self.state.get("calls", []), [])


if __name__ == "__main__":
    unittest.main()
