param([string]$Ffmpeg = 'ffmpeg')
$ErrorActionPreference = 'Stop'
$mediaDir = Join-Path $PSScriptRoot '../apps/backend/media'
New-Item -ItemType Directory -Force -Path $mediaDir | Out-Null
& $Ffmpeg -y -f lavfi -i 'testsrc2=size=960x540:rate=24' -f lavfi -i 'sine=frequency=220:sample_rate=44100' -t 20 -c:v libx264 -pix_fmt yuv420p -preset fast -crf 28 -c:a aac -af 'volume=0.08' -movflags +faststart (Join-Path $mediaDir 'test-pattern.mp4')
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar sinal de teste' }
