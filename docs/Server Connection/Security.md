# Security
## Identity
Every Client should have at least one identity.
An Identity is a ED25519 key pair containing a public key and a private key.

The public key is used to sign in to a server and sign messages.
The server may save the associated public key or it's ID to reidentify the client.