package com.margelo.nitro.{{cxxNamespace}}

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class {{modulePascalCase}} : Hybrid{{modulePascalCase}}Spec() {
  override fun hello(params: {{modulePascalCase}}Params): Promise<{{modulePascalCase}}Result> {
    val result = {{modulePascalCase}}Result(success = true, data = "Hello, ${params.message}!")
    return Promise.resolved(result)
  }
}
