Ansible Vault Integration
===
[![GitHub Release](https://img.shields.io/github/v/tag/timo-reymann/idea-ansible-vault-integration.svg?label=version)](https://github.com/timo-reymann/idea-ansible-vault-integration/releases)
[![JetBrains Plugins](https://img.shields.io/badge/JetBrains-Plugins-orange)](https://plugins.jetbrains.com/plugin/14353-ansible-vault-integration)
[![CircleCI](https://circleci.com/gh/timo-reymann/idea-ansible-vault-integration.svg?style=shield)](https://app.circleci.com/pipelines/github/timo-reymann/idea-ansible-vault-integration)

Integrate the ansible vault directly into IntelliJ IDEA with context actions for vaulting and unvaulting secrets. This
makes working with ansible-vault a breeze!

## Whats in the box?

### Vaulting secrets

Vault any text from within your yaml file, just hint ``Alt+Enter`` -> ``Vault ansible secret``

### Unvaulting secrets

Unvaulting is as easy as placing your cursor in the secret, hitting ``Alt+Enter`` -> ``Unvault ansible secret`` and you
are done!

### Configure ansible-vault call

Got a custom vault file for your project? - I got you covered with custom command line arguments!

### Provided environment variables

In case you are using a script to provide your vault secret, the plugin provides the following environment variables:

| Environment variable                  | Content                                                               |
| :------------------------------------ | :-------------------------------------------------------------------- |
| IDEA_ANSIBLE_VAULT_CONTEXT_FILE       | Absolute path to the file the vault/unvault action was triggered in   |
| IDEA_ANSIBLE_VAULT_CONTEXT_DIRECTORY  | Name of the directory the action was triggered in, **NO** path        |

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
#!/bin/bash

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

## Why?

Ansible is great, but the ansible-vault is a piece of junk to use for passwords to encrypt and decrypt.

## How can I use it?

1. Install it from the plugin repository
2. Restart your IDE
3. Load your ansible project
4. Change your configuration for the project if necessary (custom ansible-vault, arguments)
5. You are done, enjoy the magic!
