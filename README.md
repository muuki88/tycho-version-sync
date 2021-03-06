## Tycho Version Sync

Syncs MANIFEST.MF, build.properties and pom.xml for tycho builds.
This is especially handesome if you have a lot of different 
bundles and want to keep poms and manifests in sync.

The current version is just a simple commandline tool. Maybe there
will be an eclipse-plugin or hopefully a tycho-plugin.

## Run

Create an environment variable to run tycho-version-sync in any directory.

```bash

java -jar tycho-version-sync.jar 
```

or give the directory as a parameter

```bash

java -jar tycho-version-sync.jar /path/to/project
```

For unix shell users

```bash

./tycho-version-sync 

./tycho-version-sync /path/to/project
```

## Features

* Sync your `bundle-version` in the _MANIFEST.MF_ with _pom.xml_ `<version>` element.
* Sync your _exported package_ versions with the `bundle-version` in the _MANIFEST.MF_ or _pom.xml_
* Update your _imported packages_ / _required bundles_ versions
* Update your _qualifier_ in the _build.properties_ file

## Sample usages

There are some simple regex matching rules for selecting bundles:

* `com.example` := selects only the bundle _com.example_
* `com.example.*` := selects _com.example_ and all bundles starting with _com.example_
* `com.example.+` := selects all bundles starting with _com.example_ without _com.example_
* Empty string means all bundles

### Synchronize versions

In order to keep your `MANIFEST.MF`, `exported-packages` and `feature.xml`
files and headers in sync with your `pom.xml` you can run the following command

sync exported-packages versions with the `bundle-version` header.

```bash

sync all
```

This is an abreveation for the following three commands.

sync exported-packages versions with the `bundle-version` header.

```bash

sync exported-packages
```

sync `bundle-version` with the `version` attribute of the corresponding `pom.xml`.

```bash

sync manifest
```

sync `feature version` with the `version` attribute of the corresponding `pom.xml`.

```bash

sync feature
```

### Set bundle versions

Setting all bundle versions to _1.0.0.qualifier_

```bash

set bundle-version 1.0.0.qualifier
```


Setting bundle version for _com.example_ bundle to _1.0.0.qualifier_

```bash

set bundle-version com.example 1.0.0.qualifier 
```

Setting bundle version for all bundles starting with _com.example_ inclusive _com.example_ bundle to _1.0.0.qualifier_

```bash

set bundle-version com.example.* 1.0.0.qualifier
```

Setting bundle version for all bundles starting with _com.example_ exclusive _com.example_ bundle to _1.0.0.qualifier_

```bash

set bundle-version com.example.+ 1.0.0.qualifier
```

Setting bundle version for _com.example.core_ and _com.example.core_ bundle to _1.0.0.qualifier_

```bash

set bundle-version com.example.core com.example.ui 1.0.0.qualifier
```

### Set import-package and require-bundle header

Setting import-package version for _com.example.dependency_ package to _1.0.0_

```bash

set import-package com.example.dependency 1.0.0
```

Setting require-bundle version for _com.example.bundle_ package to _1.0.0_.
Note that this command will preserve all directives and attributes, like
`resolution="optional"` or `visibility:="reexport"`!

```bash

set require-bundle com.example.bundle 1.0.0
```

Will change

```properties

require-bundle: com.example;bundle-version="0.5.0";visibility:="reexport"
```

into

```properties

require-bundle: com.example;bundle-version="1.0.0";visibility:="reexport"
```

### Set feature version

You can set all your features to a different version with one command

```bash

set feature 1.0.0
```

Of course all the common regular expressions work as well

```bash

set feature com.example.* 1.0.0
```

to set all features with id _com.example.*_ to version 1.0.0.

### Set qualifier in build.properties

Setting all existing _qualifier_ properties in `build.properties` to version _none_

```bash

set qualifier none
```
