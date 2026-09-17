param(
    [ValidateSet('vod', 'live')][string]$Mode = 'vod',
    [string]$Ffmpeg = 'ffmpeg'
)
$ErrorActionPreference = 'Stop'
$repo = Split-Path $PSScriptRoot -Parent
$media = Join-Path $repo 'apps/backend/media'
$source = Join-Path $media 'test-pattern.mp4'
$output = Join-Path $media 'generated'
New-Item -ItemType Directory -Path $output -Force | Out-Null
if ($Mode -eq 'live') {
    # Ctrl+C stops the local signal. Only the project's self-generated source is read.
    & $Ffmpeg -hide_banner -loglevel warning -re -stream_loop -1 -i $source -c:v libx264 -preset ultrafast -r 24 -g 48 -sc_threshold 0 -c:a aac -f hls -hls_time 2 -hls_list_size 12 -hls_delete_threshold 6 -hls_flags delete_segments+omit_endlist+temp_file -hls_segment_filename (Join-Path $output 'live-%06d.ts') -y (Join-Path $output 'live.m3u8')
} else {
    & $Ffmpeg -hide_banner -loglevel warning -i $source -c:v libx264 -preset ultrafast -r 24 -g 48 -sc_threshold 0 -c:a aac -f hls -hls_time 2 -hls_playlist_type vod -hls_segment_filename (Join-Path $output 'vod-%03d.ts') -y (Join-Path $output 'vod.m3u8')
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar HLS' }
    $encoder = (Get-Command $Ffmpeg -ErrorAction Stop).Source
    Push-Location $output
    try {
        & $encoder -hide_banner -loglevel warning -i $source -c:v libx264 -b:v 800k -preset ultrafast -r 24 -g 48 -sc_threshold 0 -c:a aac -f dash -seg_duration 2 -y 'vod.mpd'
        if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar DASH' }
    } finally { Pop-Location }
}
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar streaming próprio' }
