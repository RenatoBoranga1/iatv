param(
  [Parameter(Mandatory=$true)][string]$Adb,
  [string]$Serial = 'emulator-5558'
)
# Structural observations only. Never emit UI text, identifiers or input values.
function Emit-Observation([string]$Component, [string]$Method, [string]$ResultType, $SafeResult) {
  [ordered]@{
    timestamp = [DateTime]::UtcNow.ToString('o')
    component = $Component
    method = $Method
    event = 'observed'
    argument_types = @()
    result_type = $ResultType
    safe_result = $SafeResult
  } | ConvertTo-Json -Compress -Depth 5
}
$processIds = & $Adb -s $Serial shell pidof nd.brasiltvnew.top
Emit-Observation 'APP' 'pidof' 'boolean' ([bool]($LASTEXITCODE -eq 0 -and $processIds))
$raw = (& $Adb -s $Serial exec-out uiautomator dump /dev/tty) -join "`n"
if ($LASTEXITCODE -ne 0) { throw 'UI observation unavailable' }
$start = $raw.IndexOf('<?xml')
$end = $raw.IndexOf('</hierarchy>')
if ($start -lt 0 -or $end -le $start) { throw 'UI observation invalid; no result recorded' }
[xml]$ui = $raw.Substring($start, $end + 12 - $start)
$userField = $ui.SelectSingleNode('//node[@resource-id="nd.brasiltvnew.top:id/de"]')
$passwordField = $ui.SelectSingleNode('//node[@resource-id="nd.brasiltvnew.top:id/dd"]')
$connectButton = $ui.SelectSingleNode('//node[@resource-id="nd.brasiltvnew.top:id/lq"]')
Emit-Observation 'UI' 'login_structure' 'object' ([ordered]@{
  login_form_present = [bool]($userField -and $passwordField -and $connectButton)
  username_present = [bool]($userField -and $userField.GetAttribute('text').Length -gt 0)
  password_present = [bool]($passwordField -and $passwordField.GetAttribute('text').Length -gt 0)
  password_masked = [bool]($passwordField -and $passwordField.GetAttribute('password') -eq 'true')
})
# These are UI/process observations, NOT CAuth/TVCore method instrumentation.
