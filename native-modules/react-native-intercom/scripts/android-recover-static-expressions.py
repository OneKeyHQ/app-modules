#!/usr/bin/env python3
"""Diagnose and optionally restore bounded static/local capture expressions from original metadata."""
import argparse, collections, hashlib, json, pathlib, subprocess

ROOT = pathlib.Path(__file__).resolve().parent.parent
BASE = ROOT / 'source/android/intercom-sdk-base'
STAGE = ROOT / '.recovery/static-capture-expressions'
MAPPING = ROOT / '.recovery/android-base-mapping'
AUDIT = pathlib.Path('/Volumes/T7Shield/Project/intercom-native-audit-20261104')
JAVA = '/opt/homebrew/opt/openjdk@17/bin/java'
JAVAC = '/opt/homebrew/opt/openjdk@17/bin/javac'
ASM = pathlib.Path('/Volumes/T7Shield/Project/jpush-jcore-sdk-recovery-20261103/tools/asm')


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def main():
    global STAGE
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apply', action='store_true')
    parser.add_argument('--stage', type=pathlib.Path)
    parser.add_argument('--sites')
    parser.add_argument('--ledger-name', default='static-expression-recovery')
    parser.add_argument('--include-fields', action='store_true')
    parser.add_argument('--expression-factories', action='store_true')
    args = parser.parse_args()
    if args.stage:
        STAGE = args.stage.resolve()
    STAGE.mkdir(exist_ok=True)
    tools = STAGE / 'tools'
    tools.mkdir(exist_ok=True)
    analysis = pathlib.Path('/Users/huhuanming/.gradle/caches/modules-2/files-2.1/org.ow2.asm/asm-analysis/9.7.1/f97a3b319f0ed6a8cd944dc79060d3912a28985f/asm-analysis-9.7.1.jar')
    assert analysis.is_file()
    classpath = ':'.join(str(path) for path in [*sorted(ASM.glob('*.jar')), analysis])
    commands = [[JAVAC, '-cp', classpath, '-d', str(tools),
                 str(ROOT / 'tools/ExportLambdaSites.java'),
                 str(ROOT / 'tools/SourceCaptureBindings.java'),
                 str(ROOT / 'tools/RecoverStaticCaptureExpressions.java')]]
    subprocess.run(commands[-1], check=True)
    bootstraps = {}
    factory_assignments = {}
    original_fields = {}
    for name in ('intercom-sdk-base', 'intercom-sdk-lightcompressor', 'nexus-client-android', 'twig'):
        artifact = ROOT / '.recovery/android-base/normalized' / (name + '-reference.jar')
        output = STAGE / (name + '-sites.tsv')
        command = [JAVA, '-cp', str(tools) + ':' + classpath, 'ExportLambdaSites', str(artifact), str(output), '--with-bci']
        commands.append(command)
        subprocess.run(command, check=True)
        for line in pathlib.Path(str(output) + '.bootstrap.tsv').read_text().splitlines():
            row = line.split('\t')
            key = tuple(row[:4])
            assert key not in bootstraps
            bootstraps[key] = row
        for line in pathlib.Path(str(output) + '.assignment.tsv').read_text().splitlines():
            row = line.split('\t')
            factory_assignments[tuple(row[:4])] = row[4:]
        for line in pathlib.Path(str(output) + '.field-captures.tsv').read_text().splitlines():
            row = line.split('\t')
            original_fields[tuple(row[:5])] = row[5:]
    rows = [line.split('\t') for line in (MAPPING / 'lambda-sites-with-bci.tsv').read_text().splitlines()]
    mappings = collections.defaultdict(list)
    for line in (MAPPING / 'capture-map.tsv').read_text().splitlines():
        row = line.split('\t')
        mappings[tuple(row[1:7])].append(row)
    field_maps = collections.defaultdict(list)
    field_capture_maps = collections.defaultdict(list)
    if args.include_fields:
        field_stage = ROOT / '.recovery/getfield-capture-mapping'
        for line in (field_stage / 'field-map.tsv').read_text().splitlines():
            row = line.split('\t')
            field_maps[tuple(row[1:7])].append(row)
        for line in (field_stage / 'capture-map.tsv').read_text().splitlines():
            row = line.split('\t')
            field_capture_maps[tuple(row[1:7])].append(row)
    handoff = AUDIT / 'independent-review/android-base/method-reference/sites.tsv'
    selected = {int(line.split('\t')[2]) for line in handoff.read_text().splitlines()[1:]}
    if args.sites:
        selected = {int(value) for value in args.sites.split(',')}
        assert all(0 <= site < len(rows) for site in selected)
    requests, evidence, skipped = [], [], []
    for index in sorted(selected):
        row = rows[index]
        nonlocal_capture = any('opcode' in value or '+' in value for value in row[11].split(','))
        if row[7] != '6' or nonlocal_capture and not args.include_fields:
            skipped.append({'site': index, 'reason': 'not a proven direct-local static factory'})
            continue
        matches = [item for item in mappings[tuple(row[:3] + row[4:7])]
                   if int(row[12]) in {int(value) for value in item[7].strip('{}').split(', ') if value}]
        assert len(matches) == 2 and {item[0] for item in matches} == {'AT_END', 'LATE_PROCESSING'} and matches[0][8:] == matches[1][8:]
        captures = [value.split('=') for value in row[11].split(',') if value]
        names = [value.split('|') for value in matches[0][8:]]
        assert len(captures) == len(names)
        field_evidence = []
        if nonlocal_capture:
            new_matches = [item for item in field_capture_maps[tuple(row[:3] + row[4:7])]
                           if int(row[12]) in {int(value) for value in item[7].strip('{}').split(', ') if value}]
            assert len(new_matches) == 2 and {item[0] for item in new_matches} == {'AT_END', 'LATE_PROCESSING'}
            assert new_matches[0][8:] == new_matches[1][8:] == matches[0][8:]
        for capture_index, ((descriptor, producer), name) in enumerate(zip(captures, names)):
            if producer.startswith('var'):
                assert name[0] == 'LOCAL' and producer == 'var' + name[1] and descriptor == name[5]
                continue
            assert args.include_fields and producer == 'opcode180'
            field_matches = [item for item in field_maps[tuple(row[:3] + row[4:7])]
                             if item[8] == str(capture_index) and int(row[12]) in {int(value) for value in item[7].strip('{}').split(', ') if value}]
            assert len(field_matches) == 2 and {item[0] for item in field_matches} == {'AT_END', 'LATE_PROCESSING'}
            assert field_matches[0][8:] == field_matches[1][8:]
            field = field_matches[0]
            receiver = field[13].split('|')
            assert field[12] == 'false' and receiver[0] == 'LOCAL'
            original_field = original_fields[tuple(row[:3] + [row[12], str(capture_index)])]
            assert original_field[:3] == field[9:12] and original_field[3] == 'var' + receiver[1]
            assert descriptor == field[11]
            names[capture_index] = ['FIELD', *receiver[1:5], descriptor, *field[9:11], receiver[5]]
            field_evidence.append({'capture_index': capture_index, 'original_field': original_field, 'ssa_field': field})
        if index == 1297:
            override = json.loads((BASE / 'java24-inference-lifetime-recovery.json').read_text())['table_lambda_capture_override']
            assert override['owner'] == row[0] and override['method'] == row[1] and override['descriptor'] == row[2]
            assert override['bci'] == int(row[12]) and override['target'] == row[4:7]
            capture_index = override['capture_index']
            assert names[capture_index][0] == 'LOCAL' and names[capture_index][1] == str(override['original_slot'])
            names[capture_index][4] = override['source_name']
            field_evidence.append({'explicit_original_lifetime_split': override})
        bootstrap = bootstraps[tuple(row[:3] + [row[12]])]
        if bootstrap[4:6] != ['java/lang/invoke/LambdaMetafactory', 'metafactory'] or bootstrap[7] != '3':
            skipped.append({'site': index, 'reason': 'bootstrap is not plain three-argument metafactory', 'bootstrap': bootstrap})
            continue
        requests.append('\t'.join([str(index), *row[:3], row[12], *row[4:7], row[10], row[8], row[9], ','.join('|'.join(name) for name in names), row[11]]))
        evidence.append({'site': index, 'original_site': row, 'ssa': matches[0], 'bootstrap': bootstrap, 'field_evidence': field_evidence})
    (STAGE / 'requests.tsv').write_text('\n'.join(requests) + '\n')
    command = [JAVA, '-cp', str(tools) + ':' + classpath, 'RecoverStaticCaptureExpressions',
               str(STAGE / 'requests.tsv'), str(BASE / 'java'), str(STAGE / 'source-edits.tsv')]
    if args.expression_factories:
        command.append('--expression-factories')
    commands.append(command)
    subprocess.run(command, check=True)
    changes = collections.defaultdict(list)
    for line in (STAGE / 'source-edits.tsv').read_text().splitlines():
        row = line.split('\t')
        changes[pathlib.Path(row[0])].append(row)
    files, helper_additions = [], []
    for file, items in changes.items():
        original = file.read_text()
        replacements, insertions = [], collections.defaultdict(list)
        for row in items:
            start, end, anchor = int(row[1]), int(row[2]), int(row[4])
            assert '::' in original[start:end]
            if row[10].startswith('FIELD:'):
                original_site = rows[int(row[6])]
                assignment = factory_assignments[tuple(original_site[:3] + [original_site[12]])]
                assert assignment[:2] == [original_site[0], row[10][6:]], (row[6], assignment, row[10])
            if row[10] == 'EXPRESSION_FACTORY':
                original_site = rows[int(row[6])]
                helper_additions.append('\t'.join(['M', original_site[0], row[13], row[14]]))
                insertions[int(row[11])].append((int(row[1]), row[12].replace('\\n', '\n')))
            replacements.append((start, end, row[3]))
            insertions[anchor].append((int(row[1]), row[5].replace('\\n', '\n')))
        for anchor, snapshots in insertions.items():
            # Keep source expression order; each site retains original capture-argument order.
            ordered = sorted(snapshots)
            replacements.append((anchor, anchor, ''.join(text for _, text in ordered)))
        positions = sorted((start, end) for start, end, _ in replacements)
        assert all(left[1] <= right[0] for left, right in zip(positions, positions[1:])), file
        changed = original
        for start, end, replacement in sorted(replacements, reverse=True):
            changed = changed[:start] + replacement + changed[end:]
        backup = STAGE / 'before' / file.relative_to(ROOT)
        if args.apply:
            backup.parent.mkdir(parents=True, exist_ok=True)
            assert not backup.exists(), 'Use a new reviewed stage before another application'
            backup.write_text(original)
            file.write_text(changed)
        files.append({'file': str(file.relative_to(ROOT)), 'before_sha256': hashlib.sha256(original.encode()).hexdigest(),
                      'after_sha256': hashlib.sha256(changed.encode()).hexdigest(), 'sites': [int(row[6]) for row in items],
                      'source_joins': items})
    helper_allowlist = BASE / 'source-helper-abi.tsv'
    helper_allowlist_before = sha(helper_allowlist)
    if args.apply and helper_additions:
        existing = helper_allowlist.read_text().splitlines()
        assert len(set(helper_additions)) == len(helper_additions)
        assert not set(existing) & set(helper_additions)
        helper_allowlist.write_text('\n'.join([*existing, *sorted(helper_additions)]) + '\n')
    receipt = {'status': 'SOURCE_EXPRESSIONS_RESTORED_NOT_COMPILED' if args.apply else 'DIAGNOSTIC_ONLY',
               'scope': 'Plain metafactory, static targets, direct locals and explicitly proved GETFIELD/local receivers; typed ordered snapshots at bounded statements with no writes to captured bindings; GETFIELD factories additionally reject earlier effectful/throwing expressions; original helper source retained',
               'normal_source_build_uses_originals_or_decompiler': False, 'source_applied': args.apply,
               'selected_sites': len(selected), 'direct_local_requests': len(requests), 'restored_sites': sum(len(items) for items in changes.values()),
               'skipped': skipped, 'rejected': pathlib.Path(str(STAGE / 'source-edits.tsv') + '.rejected.tsv').read_text().splitlines(),
               'unmatched_source_context': sorted({int(row.split('\t')[0]) for row in requests} - {int(row[6]) for items in changes.values() for row in items} - {int(row.split('\t')[0]) for row in pathlib.Path(str(STAGE / 'source-edits.tsv') + '.rejected.tsv').read_text().splitlines()}),
               'files': files, 'evidence': evidence, 'commands': commands,
               'explicit_private_static_source_factories': helper_additions,
               'reference_offset_coordinate': 'Actual byte offsets in these normalized reference jars, not universally raw-vendor offsets; ASM constant-pool serialization can change operand widths',
               'diagnostic_reference_jar_sha256': {path.name: sha(path) for path in (ROOT / '.recovery/android-base/normalized').glob('*-reference.jar')},
               'source_helper_allowlist_before_sha256': helper_allowlist_before,
               'source_helper_allowlist_after_sha256': sha(helper_allowlist),
               'tool_hashes': {str(path.relative_to(ROOT)): sha(path) for path in [ROOT / 'tools/ExportLambdaSites.java', ROOT / 'tools/RecoverStaticCaptureExpressions.java', ROOT / 'tools/SourceCaptureBindings.java', pathlib.Path(__file__)]}}
    output = BASE / (args.ledger_name + '.json') if args.apply else STAGE / 'diagnostic.json'
    output.write_text(json.dumps(receipt, indent=2) + '\n')
    print(json.dumps({key: receipt[key] for key in ('status', 'selected_sites', 'direct_local_requests', 'restored_sites')}))


if __name__ == '__main__':
    main()
