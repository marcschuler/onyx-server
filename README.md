# onyx Server

![Onyx Logo](https://github.com/marcschuler/onyx-client/blob/main/public/assets/logo/logo.png?raw=true)

onyx is a VoIP instant messanger inspired by TeamSpeak and Discord.

It's free, open source and self-hostable.

## Quick Setup
``` bash
mkdir onyx-server
cd onyx-server
curl https://github.com/marcschuler/onyx-client/blob/main/docker-compose.yml?raw=true
docker compose up -d
```

On first startup an admin token will be logged. Copy it and enter it from your client as invite code.

An extended documentation will be provided soon.

## Development server
To start a local development server, run:
```bash
mvn spring-boot:run
```
