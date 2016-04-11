Complilar pela linha de comandos:
javac <dir> 

Iniciar um peer:

java service.Peer 4449 224.0.0.6 8885 224.0.0.4 8887 224.0.0.2 8884

Iniciar a aplicação testApp:

java testApp.Start <IP>:4449 BACKUP <dir> 1
java testApp.Start <IP>:4449 RESTORE <dir>
java testApp.Start <IP>:4449 DELETE <dir>
java testApp.Start <IP>:4449 RECLAIM <dir>