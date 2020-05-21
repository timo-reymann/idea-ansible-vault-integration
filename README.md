Ansible Vault Integration
===
[![GitHub Release](https://img.shields.io/github/v/tag/timo-reymann/idea-ansible-vault-integration.svg?label=version)](https://github.com/timo-reymann/idea-ansible-vault-integration/releases)
[![JetBrains Plugins](https://img.shields.io/badge/JetBrains-Plugins-orange)](https://plugins.jetbrains.com/plugin/COMING-SOON)

Integrate the ansible vault directly into IntelliJ IDEA with context actions for vaulting and unvaulting secrets.
This makes working with ansible-vault a breeze!

## Whats in the box?

### Vaulting secrets
Vault any text from within your yaml file, just hint ``Alt+Enter`` -> ``Vault ansible secret``

### Unvaulting secrets 
Unvaulting is as easy as placing your cursor in the secret, hitting ``Alt+Enter`` -> ``Unvault ansible secret`` and you are done!

### Configure ansible-vault call
Got a custom vault file for your project? - I got you covered with custom command line arguments!

## Why?
Ansible is great, but the ansible-vault is a piece of junk to use for passwords to encrypt and decrypt.

## How can i use it?
1. Install it from the plugin repository
2. Restart your IDE
3. Load your ansible project
4. Change your configuration for the project if necessary (custom ansible-vault, arguments)
5. You are done, enjoy the magic!
