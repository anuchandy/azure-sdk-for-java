1. Copy paste this dir "AppConfiguration" under "http-client-generator-test"
2. Change "http-client-generator-test/tspconfig.yml" to

```yaml
emit:
  - "@typespec/http-client-java"
options:
  "@typespec/http-client-java":
    emitter-output-dir: "{project-root}/../../../../../azure-sdk-for-java/sdk/appconfiguration/azure-data-appconfiguration-ts-1"
    namespace: "com.azure.data.appconfiguration"
    flavor: "azure"
    dev-options:
      generate-code-model: true
      loglevel: info
      debug: false
```

3. run 

> typespec/packages/http-client-java/generator/http-client-generator-test $ tsp compile ./AppConfiguration/main.tsp --config tspconfig.yaml
 