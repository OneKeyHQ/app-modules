#!/usr/bin/env python3
"""Compare source class ConstantValue attributes against reviewed text metadata; never repair them."""
import argparse
import hashlib
import json
import re
from pathlib import Path
from export_field_constants import fields


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('classes', type=Path)
    parser.add_argument('expected', type=Path)
    parser.add_argument('report', type=Path)
    parser.add_argument('--aliases', type=Path)
    args = parser.parse_args()
    types, members = {}, {}
    if args.aliases:
        for line in args.aliases.read_text().splitlines():
            row = line.split('\t')
            if row[0] == 'C':
                types[row[2]] = row[1]
            elif row[0] == 'F':
                members[(row[1], row[4], row[3])] = row[2]

    def descriptor(text):
        return re.sub(r'L([^;]+);', lambda m: 'L' + types.get(m[1], m[1]) + ';', text)

    expected = {(r['owner'], r['name'], r['descriptor']): r['constant_value'] for r in json.loads(args.expected.read_text())['fields']}
    expected_owners = {key[0] for key in expected}
    actual, owners, extras, inputs = {}, set(), [], []
    for path in sorted(args.classes.rglob('*.class')):
        owner, rows = fields(path.read_bytes())
        owner = types.get(owner, owner)
        inputs.append(dict(path=str(path.relative_to(args.classes)), sha256=hashlib.sha256(path.read_bytes()).hexdigest()))
        if owner not in expected_owners:
            extras.append(owner)
            continue
        owners.add(owner)
        for row in rows:
            desc = descriptor(row['descriptor'])
            name = members.get((owner, row['name'], desc), row['name'])
            key = (owner, name, desc)
            if key in actual:
                raise RuntimeError('Duplicate normalized field: ' + repr(key))
            actual[key] = row['constant_value']
    missing = [list(key) for key in expected if key[0] in owners and key not in actual]
    extra_fields = [list(key) for key in actual if key not in expected]
    differences = [dict(field=list(key), expected=expected[key], actual=value) for key, value in actual.items()
                   if key in expected and value != expected[key]]
    report = dict(status='PASS_CONSTANT_VALUES' if not differences and not missing and not extra_fields else 'FAIL',
                  scope='Only actual original owner fields; source-only compiler helpers separately inventoried. Matching ConstantValue does not prove original initialization order or method behavior.',
                  original_owners_present=len(owners), compared_fields=len(actual), missing_fields=missing,
                  extra_fields=extra_fields, differences=differences, owners_without_expected_fields=extras, input_hashes=inputs)
    args.report.write_text(json.dumps(report, ensure_ascii=True, indent=2) + '\n')
    print(json.dumps({k: v for k, v in report.items() if k not in ['input_hashes', 'owners_without_expected_fields']}, ensure_ascii=True))
    return 0 if report['status'] == 'PASS_CONSTANT_VALUES' else 1


if __name__ == '__main__':
    raise SystemExit(main())
