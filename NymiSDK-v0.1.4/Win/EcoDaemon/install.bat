copy "%~dp0ecodaemon.exe" c:\
sc create BgEcodaemon binPath= c:\ecodaemon.exe start= auto
sc start BgEcodaemon
@pause