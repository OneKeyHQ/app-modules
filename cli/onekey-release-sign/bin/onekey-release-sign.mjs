#!/usr/bin/env node
import { runCli } from '../src/cli.mjs';

const result = runCli(process.argv.slice(2));
if (result.stdout) {
  process.stdout.write(result.stdout);
}
if (result.stderr) {
  process.stderr.write(result.stderr);
}
process.exitCode = result.exitCode;
