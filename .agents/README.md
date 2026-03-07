## Project Overview
Esse projeto é sobre RFID e arduino uno, ele é divido em 3 modulos

1- Modulo Arduino 
responsavel por ler e cadastrar os cartoes rfid

2- Modulo java desktop
 -responsavel por contactar o arduino para pegar os valores e controloar a leitura ou o cadastro
- responsavel que quando no modo cadastro , ele envia um comando para o arduino para cadastrar o cartao rfid
- responsavel que quando no modo leitura , ele envia um comando para o arduino para ler o cartao rfid
- responsavel por enviar os dados para o modulo web

3- Modulo PHP Web
 - modulo php laravel que disponibiliza uma api + web socket
 - responsavel por receber os dados do modulo java desktop
 - responsavel por enviar os dados para o modulo java desktop
 - responsavel por armazenar os cartoes rfid
 - responsavel pelo painel de controle
  