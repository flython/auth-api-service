# auth-api-doc

**Description**:auth-api-doc

**HOST**:http://localhost:8086

**Contacts**:

**Version**:1.0

**URL**:/v3/api-docs?group=all

[TOC]

# auth-controller

## authorize

**url**:`/authentication`

**method**:`POST`

**produces**:`application/x-www-form-urlencoded,application/json`

**consumes**:`*/*`

**Note**:

**Example**:

```javascript
{
    "password"
:
    "",
        "userName"
:
    ""
}
```

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|authReq|AuthReq|body|true|AuthReq|AuthReq|
|&emsp;&emsp;password|||false|string||
|&emsp;&emsp;userName|||false|string||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK|AuthResp|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||

**Response Params**:

| name | description | type | schema |
| -------- | -------- | ----- |----- | 
|expireTs||integer(int64)|integer(int64)|
|token||string||

**Response Example**:

```javascript
{
    "expireTs"
:
    0,
        "token"
:
    ""
}
```

## invalidate

**url**:`/authentication`

**method**:`DELETE`

**produces**:`application/x-www-form-urlencoded`

**consumes**:`*/*`

**Note**:

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|authorization|authorization|query|false|string||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|204|No Content||
|401|Unauthorized||
|403|Forbidden||

**Response Params**:

None

**Response Example**:

```javascript

```

## allRoles

**url**:`/authentication/allRoles`

**method**:`GET`

**produces**:`application/x-www-form-urlencoded`

**consumes**:`*/*`

**Note**:

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||

**Response Params**:

None

**Response Example**:

```javascript

```

## checkRole

**url**:`/authentication/checkRole`

**method**:`GET`

**produces**:`application/x-www-form-urlencoded`

**consumes**:`*/*`

**Note**:

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization||header|false|||
|role|role|query|false|string||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||

**Response Params**:

None

**Response Example**:

```javascript

```

# role-controller

## createRole

**url**:`/role`

**method**:`POST`

**produces**:`application/x-www-form-urlencoded,application/json`

**consumes**:`*/*`

**Note**:

**Example**:

```javascript
{
    "roleName"
:
    ""
}
```

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createRoleReq|CreateRoleReq|body|true|CreateRoleReq|CreateRoleReq|
|&emsp;&emsp;roleName|||false|string||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||

**Response Params**:

None

**Response Example**:

```javascript

```

## bindRole2User

**url**:`/role/binding`

**method**:`POST`

**produces**:`application/x-www-form-urlencoded,application/json`

**consumes**:`*/*`

**Note**:

**Example**:

```javascript
{
    "roleName"
:
    "",
        "userId"
:
    0
}
```

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|bindRoleReq|BindRoleReq|body|true|BindRoleReq|BindRoleReq|
|&emsp;&emsp;roleName|||false|string||
|&emsp;&emsp;userId|||true|integer(int64)||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||

**Response Params**:

None

**Response Example**:

```javascript

```

## unBindRole

**url**:`/role/binding/{roleName}`

**method**:`DELETE`

**produces**:`application/x-www-form-urlencoded`

**consumes**:`*/*`

**Note**:

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleName|roleName|path|true|string||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|204|No Content||
|401|Unauthorized||
|403|Forbidden||

**Response Params**:

None

**Response Example**:

```javascript

```

## deleteRole

**url**:`/role/{roleName}`

**method**:`DELETE`

**produces**:`application/x-www-form-urlencoded`

**consumes**:`*/*`

**Note**:

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleName|roleName|path|true|string||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|204|No Content||
|401|Unauthorized||
|403|Forbidden||

**Response Params**:

None

**Response Example**:

```javascript

```

# user-controller

## createUser

**url**:`/user`

**method**:`POST`

**produces**:`application/x-www-form-urlencoded,application/json`

**consumes**:`*/*`

**Note**:

**Example**:

```javascript
{
    "password"
:
    "",
        "userName"
:
    ""
}
```

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createUserReq|CreateUserReq|body|true|CreateUserReq|CreateUserReq|
|&emsp;&emsp;password|||false|string||
|&emsp;&emsp;userName|||false|string||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK|CreateUserResp|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||

**Response Params**:

| name | description | type | schema |
| -------- | -------- | ----- |----- | 
|userId||integer(int64)|integer(int64)|
|userName||string||

**Response Example**:

```javascript
{
    "userId"
:
    0,
        "userName"
:
    ""
}
```

## deleteUser

**url**:`/user/{userId}`

**method**:`DELETE`

**produces**:`application/x-www-form-urlencoded`

**consumes**:`*/*`

**Note**:

**Params**:

| name | description | in    | require | type | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId|userId|path|true|integer(int64)||
|Authorization||header|false|||

**Status**:

| code | description | schema |
| -------- | -------- | ----- | 
|200|OK||
|204|No Content||
|401|Unauthorized||
|403|Forbidden||

**Response Params**:

None

**Response Example**:

```javascript

```