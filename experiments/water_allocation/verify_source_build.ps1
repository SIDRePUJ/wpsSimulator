# Compile the isolated water-allocation worktree against local BESA sources.
# This deliberately does not invoke root scripts/build.ps1 or overwrite root bin/.
$ErrorActionPreference = 'Stop'
$Worktree = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..'))
$Root = [System.IO.Path]::GetFullPath((Join-Path $Worktree '..\..'))
$Output = Join-Path $Root ('tools\water-allocation-build-' + [guid]::NewGuid().ToString('N'))
$Javac = Join-Path $Root 'tools\jdk-win\bin\javac.exe'
$Java = Join-Path $Root 'tools\jdk-win\bin\java.exe'
$Libs = (Get-ChildItem -LiteralPath (Join-Path $Root 'lib') -Filter '*.jar' -File |
    ForEach-Object FullName) -join ';'

if (-not (Test-Path -LiteralPath $Javac)) { throw "Java compiler not found: $Javac" }
if (Test-Path -LiteralPath $Output) { throw "Refusing to reuse build output: $Output" }
New-Item -ItemType Directory -Path (Join-Path $Output 'besa'),
    (Join-Path $Output 'wps'),(Join-Path $Output 'tests') -Force | Out-Null

function Write-SourceList($Paths, $File) {
    $Paths | ForEach-Object { '"' + $_.Replace([char]92, [char]47) + '"' } |
        Set-Content -LiteralPath $File -Encoding ascii
}

foreach ($Module in @('KernelBESA','LocalBESA','RemoteBESA','RationalBESA','BDIBESA','eBDIBESA')) {
    $Sources = Get-ChildItem -LiteralPath (Join-Path $Root "repos\$Module\src") `
        -Filter '*.java' -File -Recurse |
        Where-Object { $_.FullName -notlike '*\test\*' } |
        ForEach-Object FullName
    $Arguments = Join-Path $Output "$Module.txt"
    Write-SourceList $Sources $Arguments
    & $Javac -nowarn -encoding UTF-8 -proc:none -d (Join-Path $Output 'besa') `
        -cp "$(Join-Path $Output 'besa');$Libs" "@$Arguments"
    if ($LASTEXITCODE -ne 0) { throw "BESA compilation failed: $Module" }
    Write-Output "BESA $Module PASS"
}

$Sources = Get-ChildItem -LiteralPath (Join-Path $Worktree 'src\main\java') `
    -Filter '*.java' -File -Recurse |
    Where-Object { $_.FullName -notlike '*\ViewerLens\Server\WebsocketServer.java' } |
    ForEach-Object FullName
$Sources += Join-Path $Root 'patches\headless\WebsocketServer.java'
$Arguments = Join-Path $Output 'wps.txt'
Write-SourceList $Sources $Arguments
& $Javac -nowarn -encoding UTF-8 -proc:none -d (Join-Path $Output 'wps') `
    -cp "$(Join-Path $Output 'besa');$Libs" "@$Arguments"
if ($LASTEXITCODE -ne 0) { throw 'Water-allocation WellProdSim compilation failed' }
Write-Output "WellProdSim PASS ($($Sources.Count) files)"

$Test = Join-Path $Worktree 'src\test\java\org\wpsim\AgroEcosystem\layer\crop\CropLayerIrrigationTest.java'
$Classpath = "$(Join-Path $Output 'tests');$(Join-Path $Output 'wps');" +
    "$(Join-Path $Output 'besa');$Libs;$(Join-Path $Worktree 'src\main\resources')"
& $Javac -d (Join-Path $Output 'tests') -cp $Classpath $Test
if ($LASTEXITCODE -ne 0) { throw 'CropLayerIrrigationTest compilation failed' }
Push-Location $Worktree
try {
    & $Java -ea -cp $Classpath org.wpsim.AgroEcosystem.layer.crop.CropLayerIrrigationTest
    if ($LASTEXITCODE -ne 0) { throw 'CropLayerIrrigationTest failed' }
} finally {
    Pop-Location
}
Write-Output "Isolated build output: $Output"
