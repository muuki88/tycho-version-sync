## Tycho Version Sync

Syncs MANIFEST.MF, build.properties and pom.xml for tycho builds.
This is especially handesome if you have a lot of different 
bundles and want to keep poms and manifests in sync.

The current version is just a simple commandline tool. Maybe there
will be an eclipse-plugin or hopefully a tycho-plugin.

## Run

Create an environment variable to run tycho-version-sync
in any directory.

```bash

java -jar tycho-version-sync.jar 
```

or give the directory as a parameter

```bash

java -jar tycho-version-sync.jar /home/user/my/path
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

sync exported-packages versions with the `bundle-version` header.

```bash

sync exported-packages
```

sync `bundle-version` with the `version` attribute of the corresponding `pom.xml`.

```bash

sync manifest
```

### Set bundle versions

Setting all bundle versions to _1.0.0.qualifier_

```bash

set bundle-version 1.0.0.qualifier
```


Setting bundle version for _com.example_ bundle to _1.0.0.qualifier_

```bash

set bundle-version 1.0.0.qualifier com.example
```

Setting bundle version for all bundles starting with _com.example_ inclusive _com.example_ bundle to _1.0.0.qualifier_

```bash

set bundle-version 1.0.0.qualifier  com.example.*
```

Setting bundle version for all bundles starting with _com.example_ exclusive _com.example_ bundle to _1.0.0.qualifier_

```bash

set bundle-version 1.0.0.qualifier com.example.+
```

Setting bundle version for _com.example.core_ and _com.example.core_ bundle to _1.0.0.qualifier_

```bash

set bundle-version 1.0.0.qualifier com.example.core com.example.ui
```


### Set qualifier in build.properties

Setting all existing _qualifier_ properties in `build.properties` to version _none_

```bash

set qualifier none
```
