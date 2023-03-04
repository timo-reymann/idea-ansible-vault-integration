Ansible Vault Integration
===
[![CircleCI](https://circleci.com/gh/timo-reymann/idea-ansible-vault-integration.svg?style=shield)](https://app.circleci.com/pipelines/github/timo-reymann/idea-ansible-vault-integration)
[![GitHub Release](https://img.shields.io/github/v/tag/timo-reymann/idea-ansible-vault-integration.svg?label=version)](https://github.com/timo-reymann/idea-ansible-vault-integration/releases)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/14353-ansible-vault-integration)](https://plugins.jetbrains.com/plugin/14353-ansible-vault-integration)
[![Rating](https://img.shields.io/jetbrains/plugin/r/rating/14353-ansible-vault-integration)](https://plugins.jetbrains.com/plugin/14353-ansible-vault-integration/reviews)
[![Renovate](https://img.shields.io/badge/renovate-enabled-green?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAzNjkgMzY5Ij48Y2lyY2xlIGN4PSIxODkuOSIgY3k9IjE5MC4yIiByPSIxODQuNSIgZmlsbD0iI2ZmZTQyZSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTUgLTYpIi8+PHBhdGggZmlsbD0iIzhiYjViNSIgZD0iTTI1MSAyNTZsLTM4LTM4YTE3IDE3IDAgMDEwLTI0bDU2LTU2YzItMiAyLTYgMC03bC0yMC0yMWE1IDUgMCAwMC03IDBsLTEzIDEyLTktOCAxMy0xM2ExNyAxNyAwIDAxMjQgMGwyMSAyMWM3IDcgNyAxNyAwIDI0bC01NiA1N2E1IDUgMCAwMDAgN2wzOCAzOHoiLz48cGF0aCBmaWxsPSIjZDk1NjEyIiBkPSJNMzAwIDI4OGwtOCA4Yy00IDQtMTEgNC0xNiAwbC00Ni00NmMtNS01LTUtMTIgMC0xNmw4LThjNC00IDExLTQgMTUgMGw0NyA0N2M0IDQgNCAxMSAwIDE1eiIvPjxwYXRoIGZpbGw9IiMyNGJmYmUiIGQ9Ik04MSAxODVsMTgtMTggMTggMTgtMTggMTh6Ii8+PHBhdGggZmlsbD0iIzI1YzRjMyIgZD0iTTIyMCAxMDBsMjMgMjNjNCA0IDQgMTEgMCAxNkwxNDIgMjQwYy00IDQtMTEgNC0xNSAwbC0yNC0yNGMtNC00LTQtMTEgMC0xNWwxMDEtMTAxYzUtNSAxMi01IDE2IDB6Ii8+PHBhdGggZmlsbD0iIzFkZGVkZCIgZD0iTTk5IDE2N2wxOC0xOCAxOCAxOC0xOCAxOHoiLz48cGF0aCBmaWxsPSIjMDBhZmIzIiBkPSJNMjMwIDExMGwxMyAxM2M0IDQgNCAxMSAwIDE2TDE0MiAyNDBjLTQgNC0xMSA0LTE1IDBsLTEzLTEzYzQgNCAxMSA0IDE1IDBsMTAxLTEwMWM1LTUgNS0xMSAwLTE2eiIvPjxwYXRoIGZpbGw9IiMyNGJmYmUiIGQ9Ik0xMTYgMTQ5bDE4LTE4IDE4IDE4LTE4IDE4eiIvPjxwYXRoIGZpbGw9IiMxZGRlZGQiIGQ9Ik0xMzQgMTMxbDE4LTE4IDE4IDE4LTE4IDE4eiIvPjxwYXRoIGZpbGw9IiMxYmNmY2UiIGQ9Ik0xNTIgMTEzbDE4LTE4IDE4IDE4LTE4IDE4eiIvPjxwYXRoIGZpbGw9IiMyNGJmYmUiIGQ9Ik0xNzAgOTVsMTgtMTggMTggMTgtMTggMTh6Ii8+PHBhdGggZmlsbD0iIzFiY2ZjZSIgZD0iTTYzIDE2N2wxOC0xOCAxOCAxOC0xOCAxOHpNOTggMTMxbDE4LTE4IDE4IDE4LTE4IDE4eiIvPjxwYXRoIGZpbGw9IiMzNGVkZWIiIGQ9Ik0xMzQgOTVsMTgtMTggMTggMTgtMTggMTh6Ii8+PHBhdGggZmlsbD0iIzFiY2ZjZSIgZD0iTTE1MyA3OGwxOC0xOCAxOCAxOC0xOCAxOHoiLz48cGF0aCBmaWxsPSIjMzRlZGViIiBkPSJNODAgMTEzbDE4LTE3IDE4IDE3LTE4IDE4ek0xMzUgNjBsMTgtMTggMTggMTgtMTggMTh6Ii8+PHBhdGggZmlsbD0iIzk4ZWRlYiIgZD0iTTI3IDEzMWwxOC0xOCAxOCAxOC0xOCAxOHoiLz48cGF0aCBmaWxsPSIjYjUzZTAyIiBkPSJNMjg1IDI1OGw3IDdjNCA0IDQgMTEgMCAxNWwtOCA4Yy00IDQtMTEgNC0xNiAwbC02LTdjNCA1IDExIDUgMTUgMGw4LTdjNC01IDQtMTIgMC0xNnoiLz48cGF0aCBmaWxsPSIjOThlZGViIiBkPSJNODEgNzhsMTgtMTggMTggMTgtMTggMTh6Ii8+PHBhdGggZmlsbD0iIzAwYTNhMiIgZD0iTTIzNSAxMTVsOCA4YzQgNCA0IDExIDAgMTZMMTQyIDI0MGMtNCA0LTExIDQtMTUgMGwtOS05YzUgNSAxMiA1IDE2IDBsMTAxLTEwMWM0LTQgNC0xMSAwLTE1eiIvPjxwYXRoIGZpbGw9IiMzOWQ5ZDgiIGQ9Ik0yMjggMTA4bC04LThjLTQtNS0xMS01LTE2IDBMMTAzIDIwMWMtNCA0LTQgMTEgMCAxNWw4IDhjLTQtNC00LTExIDAtMTVsMTAxLTEwMWM1LTQgMTItNCAxNiAweiIvPjxwYXRoIGZpbGw9IiNhMzM5MDQiIGQ9Ik0yOTEgMjY0bDggOGM0IDQgNCAxMSAwIDE2bC04IDdjLTQgNS0xMSA1LTE1IDBsLTktOGM1IDUgMTIgNSAxNiAwbDgtOGM0LTQgNC0xMSAwLTE1eiIvPjxwYXRoIGZpbGw9IiNlYjZlMmQiIGQ9Ik0yNjAgMjMzbC00LTRjLTYtNi0xNy02LTIzIDAtNyA3LTcgMTcgMCAyNGw0IDRjLTQtNS00LTExIDAtMTZsOC04YzQtNCAxMS00IDE1IDB6Ii8+PHBhdGggZmlsbD0iIzEzYWNiZCIgZD0iTTEzNCAyNDhjLTQgMC04LTItMTEtNWwtMjMtMjNhMTYgMTYgMCAwMTAtMjNMMjAxIDk2YTE2IDE2IDAgMDEyMiAwbDI0IDI0YzYgNiA2IDE2IDAgMjJMMTQ2IDI0M2MtMyAzLTcgNS0xMiA1em03OC0xNDdsLTQgMi0xMDEgMTAxYTYgNiAwIDAwMCA5bDIzIDIzYTYgNiAwIDAwOSAwbDEwMS0xMDFhNiA2IDAgMDAwLTlsLTI0LTIzLTQtMnoiLz48cGF0aCBmaWxsPSIjYmY0NDA0IiBkPSJNMjg0IDMwNGMtNCAwLTgtMS0xMS00bC00Ny00N2MtNi02LTYtMTYgMC0yMmw4LThjNi02IDE2LTYgMjIgMGw0NyA0NmM2IDcgNiAxNyAwIDIzbC04IDhjLTMgMy03IDQtMTEgNHptLTM5LTc2Yy0xIDAtMyAwLTQgMmwtOCA3Yy0yIDMtMiA3IDAgOWw0NyA0N2E2IDYgMCAwMDkgMGw3LThjMy0yIDMtNiAwLTlsLTQ2LTQ2Yy0yLTItMy0yLTUtMnoiLz48L3N2Zz4=)](https://renovatebot.com)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=timo-reymann_idea-ansible-vault-integration&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=timo-reymann_idea-ansible-vault-integration)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=timo-reymann_idea-ansible-vault-integration&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=timo-reymann_idea-ansible-vault-integration)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Ftimo-reymann%2Fidea-ansible-vault-integration.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Ftimo-reymann%2Fidea-ansible-vault-integration?ref=badge_shield)

<p align="center">
	<img width="300" src="./src/main/resources/META-INF/pluginIcon.svg">
    <br />
	Integrate the ansible vault directly into IntelliJ IDEA with context actions for vaulting and unvaulting secrets. This
	makes working with ansible-vault a breeze!
</p>

## Features

### Vaulting secrets

Vault any text from within your yaml file, just hint ``Alt+Enter`` -> ``Vault ansible secret``

### Unvaulting secrets

Unvaulting is as easy as placing your cursor in the secret, hitting ``Alt+Enter`` -> ``Unvault ansible secret`` and you
are done!

## Requirements
- IDEA-based IDE compatible with the plugin

## Installation
1. Press (Ctrl+Alt+S/⌘/) to open the IDE settings and select Plugins.
2. Search for `MJML Support` in the Marketplace and click Install.

## Usage

### Configure ansible-vault call

Got a custom vault file for your project? - I got you covered with custom command line arguments!

### Provided environment variables

In case you are using a script to provide your vault secret, the plugin provides the following environment variables:

| Environment variable                         | Content                                                             |
|:---------------------------------------------|:--------------------------------------------------------------------|
| IDEA_ANSIBLE_VAULT_CONTEXT_FILE              | Absolute path to the file the vault/unvault action was triggered in |
| IDEA_ANSIBLE_VAULT_CONTEXT_DIRECTORY         | Name of the directory the action was triggered in, **NO** path      |
| IDEA_ANSIBLE_VAULT_CONTEXT_PROJECT_BASE_PATH | Absolute path of the project the action was triggered in            |
| IDEA_ANSIBLE_VAULT_CONTEXT_PROJECT_NAME      | Name of the project the action was triggered in                    |

#### Examples

Navigate to `Settings | Tools | Ansible Vault`

##### Configure secret file in current project

Use following cli args:

```
--vault-password-file .project-secret
```

##### Configure secret file in home directory

Use following cli args:

```
--vault-password-file ~/.ansible-secret
```

##### Configure secret based on maturity

Let's say you have an ansible setup with three stages (dev, qa, prod), with the following directory structure:

```
group-vars/
    all/vars.yml
    dev/vars.yml
    qa/vars.yml
    prod/vars.yml
```

For each maturity you have a different vault file following this pattern: `.${maturity}.secret`, you can use the following
configuration:

Cli args:
```
--vault-password-file .idea-get-vault-password.sh
```

Create the file `.idea-get-vault-password.sh` (0700):

```bash
#!/usr/bin/env bash

# Helper to show error message
__error_message() {
   >&2 echo "$1"
   exit 2
}

# Check script is not called directly
if [ -z "$IDEA_ANSIBLE_VAULT_CONTEXT_DIRECTORY" ]
then
  __error_message "Call is not coming from IntelliJ Plugin"
fi

# Check context folder
case "$IDEA_ANSIBLE_VAULT_CONTEXT_DIRECTORY" in
  # known maturities
  dev|qa|prod)
    secret_file=".${IDEA_ANSIBLE_VAULT_CONTEXT_DIRECTORY}.secret"
    if [ -f "$secret_file" ]
    then
      cat  ".${IDEA_ANSIBLE_VAULT_CONTEXT_DIRECTORY}.secret"
    else
      __error_message "Secret file '${secret_file}' not found"
    fi
    ;;

  # whoops something went wrong
  *)
    __error_message "Unsupported folder"
    exit 2
    ;;
esac
```

## Motivation
Ansible is great, but the ansible-vault is a piece of junk to use for passwords to encrypt and decrypt.

## Contributing
I love your input! I want to make contributing to this project as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the configuration
- Submitting a fix
- Proposing new features
- Becoming a maintainer

To get started please read the [Contribution Guidelines](./CONTRIBUTING.md).

## Development

### Requirements
- [Java](https://openjdk.org/)

### Test
```shell
# To run unit tests
./gradlew test

# To run plugin verifier to check compability
./gradlew runPluginVerifier
```

### Build
```shell
./gradlew buildPlugin
```
