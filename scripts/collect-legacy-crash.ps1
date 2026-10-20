param(
  [Parameter(Mandatory=$true)][string]$Adb,
  [string]$Serial = 'emulator-5556'
)
# Read into memory; only whitelisted stack-frame structure leaves the process.
# No raw logcat, messages containing URLs, payloads or app data are persisted.
$raw = & $Adb -s $Serial logcat -d -b crash
if ($LASTEXITCODE -ne 0) { throw 'ADB crash buffer unavailable; no result recorded.' }
# Select one process instance of this package, not crashes from other apps.
$crashPid = $null
foreach ($line in $raw) {
  if ($line -match '^\S+\s+\S+\s+(\d+)\s+\d+\s+[A-Z]\s+\S+:\s+Process: nd\.brasiltvnew\.top,') {
    $crashPid = $Matches[1]
    break
  }
}
if (!$crashPid) { Write-Output 'NOT_OBSERVED: no matching process crash in this buffer'; return }
$frames = foreach ($line in $raw) {
  if ($line -notmatch ('^\S+\s+\S+\s+' + $crashPid + '\s+')) { continue }
  if ($line -match '(?:Caused by: )?java\.lang\.ClassCastException: java\.lang\.Long cannot be cast to java\.lang\.Integer') {
    'java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Integer'
  } elseif ($line -match '\bat ((?:[A-Za-z_$][A-Za-z0-9_$]*\.)+[A-Za-z0-9_$<>]+\((?:[A-Za-z0-9_$.-]+:\d+|Native Method|Unknown Source)\))') {
    'at ' + $Matches[1]
  }
}
$frames | Select-Object -Unique
