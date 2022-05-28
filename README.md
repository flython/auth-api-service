# Auth-API

## How to use

1. import as a maven project, run by maven, than the API documentation will be displayed under this
   address: `http://localhost:8086/doc.html`，or you can refer [here](./api_doc.md)
2. provide annotated  `@PreAuthorize`, the API marked by it needs to have `Authorization: Bearer {token}` in the header
   and pass the checksum

## Core Algorithm

- User password storage
    - Using MD5 + salt, first hash and duplicate, then hash the password and store it in the auth field
    - fore example:
        - pwd: `abcdefg`
        - salt: `ABCDEFGHIJ` (random in code, here just a sample)
        - before md5: `aGBC5CD_ABCDEFGHIJbcdefgcABCDEFGHIJ`
        - after md5: `1602c8a169601e1ef6f8d85d42e396e7`
- Token generation
    - token object `Token`

        ``` json
                  {"id":3, "userId":1, "roles":[], "expire":1653715534029}
        ```
        - id: each token instance will have a system-generated id, used to record the early expiration
        - expire: expiration time, in ms
        - userId: the unique ID that user got when registering
        - roles: roles that have been bound to user
    - When logging in, after verifying the user name and password successfully, a Token will be constructed and a Token
      string will be generated according to the JWT format (but not strictly according to the JWT specification)
        - input: `MD5.{"id":2, "userId":1, "roles":[], "expire":1653716592399}.d69c7a3b3066fc8a46635c4ba483512a`
        -
      output: `Bearer 77573A66F266C14BB3086B07D8CA73A1E93CBA84943342B0BE283EEFEBD5921297489146424A46ECF42C8DF87C4AA2E551C663B7CB3CC5F7CABE6D5FBFE20C429B2602347A8DD519C238381221FE36D32D2E6160F68C8736656CE38169C2C578 `
    - Token can then be used in the Header to propagate
        - Header
          ```
          Authorization: Bearer D9EB40F8F82D4BA3A9244B58A302B040E93CBA84943342B0BE283EEFEBD5921297489146424A46ECF42C8DF87C4AA2E5942BE5AF42ED1275EBBCC6972FC8BBA2852F21621A761A47AE038D88940F0D175CAD0339CDABB954E47DDBA7F4F7F397
          ```

## dependency

| dependency | description |
| ------------------------------ | ---------------------------------------------------- |
| SpringBoot | MVC framework, in order to quickly build the backbone of the project |
| validation-api | JSR-303 javax.validation To implement interface validation |
| spring-boot-starter-validation | Implementation of JSR-303 |
| lombok | Simplified POJO Wrapping |
| spring-boot-starter-aop | Implementing Annotated Token Authentication |
| mapstruct | Automatic generation of POJO Mapping code |
| jUnit5 | testing framework |
| swagger + knife4j | A nice swagger-ui, easy to view interface documentation and online debugging |

## Optimization ideas

1. add expression support to `@PreAuthorize`, you can do identity verification by expression in `@PreAuthorize`, such as
    1. some identities can be accessed
    2. some identities cannot be accessed
2. grant certain privileges to identities, and process `@PreAuthorize` with a check against the privileges owned by the
   role instead of the identity binding roles

