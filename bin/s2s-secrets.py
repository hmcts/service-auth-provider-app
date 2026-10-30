"""Metadata-only checks and missing-secret provisioning for the two shell entry points."""

import argparse
import base64
import json
import os
import re
import subprocess
import sys
import tempfile

ENVIRONMENTS = ("sandbox", "demo", "aat", "prod", "perftest", "ithc")


class VaultError(Exception):
    pass


def azure(operation, vault, arguments):
    result = subprocess.run(
        ["az", "keyvault", "secret", operation, "--vault-name", vault,
         "--only-show-errors"] + arguments,
        stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True,
    )
    if result.returncode:
        # Never echo Azure's response to a write: it may contain secret material.
        raise VaultError("{} failed for {} (Azure CLI exit {}). No automatic retry; "
                         "check access and rerun the metadata checker."
                         .format(operation, vault, result.returncode))
    return result.stdout


def metadata(vault, name):
    # Key Vault identifiers are case-insensitive; JMESPath equality is not.
    query = "[].{name:name,enabled:attributes.enabled}"
    raw = azure("list", vault, ["--query", query, "--output", "json"])
    try:
        rows = json.loads(raw)
    except ValueError:
        raise VaultError("Invalid metadata returned by list for {}".format(vault)) from None
    if (not isinstance(rows, list) or
            any(not isinstance(row, dict) or not isinstance(row.get("name"), str) for row in rows)):
        raise VaultError("Unexpected metadata returned by list for {}".format(vault))
    matches = [row for row in rows if row["name"].lower() == name.lower()]
    if len(matches) > 1:
        raise VaultError("Ambiguous metadata for {} in {}".format(name, vault))
    return matches[0] if matches else None


def exists(vault, name):
    row = metadata(vault, name)
    if row is None:
        return False
    if row.get("enabled") is not True:
        raise VaultError("{} in {} is disabled or has unknown enabled status; leaving it unchanged."
                         .format(name, vault))
    return True


def provisionable(vault, name):
    return not exists(vault, name)


def create(vault, name):
    # A private file keeps the value out of process arguments and shell traces. Each vault gets
    # independent randomness. NamedTemporaryFile removes it on success or a handled failure.
    with tempfile.NamedTemporaryFile(mode="w", encoding="ascii", prefix="s2s-secret-") as secret:
        secret.write(base64.b32encode(os.urandom(10)).decode("ascii"))
        secret.flush()
        azure("set", vault, ["--name", name, "--file", secret.name,
                             "--encoding", "utf-8", "--output", "none"])
    if not exists(vault, name):
        raise VaultError("{} was not found in {} after creation; refusing success."
                         .format(name, vault))
    print("Created {} in {}".format(name, vault), flush=True)


def check(name):
    for environment in ENVIRONMENTS:
        vault = "s2s-" + environment
        if not exists(vault, name):
            raise VaultError("{} is missing from {}".format(name, vault))
        print("Found enabled {} in {}".format(name, vault), flush=True)


def ensure(name):
    missing = []
    # Finish the read-only preflight before the first write, so an inaccessible later vault
    # cannot be mistaken for an absent secret after earlier environments have been changed.
    for environment in ENVIRONMENTS:
        vault = "s2s-" + environment
        if provisionable(vault, name):
            missing.append(vault)
            print("Missing {} in {}".format(name, vault), flush=True)
        else:
            print("Preserved existing {} in {}".format(name, vault), flush=True)
    for vault in missing:
        # Recheck after preflight. Azure CLI set is not an atomic create-only operation:
        # operators must not provision the same service concurrently (see README).
        if provisionable(vault, name):
            create(vault, name)
        else:
            print("Preserved newly discovered {} in {}".format(name, vault), flush=True)
    check(name)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("operation", choices=("ensure", "check"))
    parser.add_argument("service", help="Service name with hyphens, e.g. deed-poll-api")
    args = parser.parse_args()
    name = "microservicekey-" + args.service
    if not re.fullmatch(r"[A-Za-z0-9-]+", args.service) or len(name) > 127:
        parser.error("Service name must contain only letters, digits and hyphens; secret name must be at most 127 characters.")
    try:
        if args.operation == "ensure":
            ensure(name)
        else:
            check(name)
    except (VaultError, OSError) as error:
        print("ERROR: {}".format(error), file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
