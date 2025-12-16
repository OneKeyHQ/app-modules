import NitroModules

class {{modulePascalCase}}: Hybrid{{modulePascalCase}}Spec {
    
    public func hello(params: {{modulePascalCase}}Params) throws -> Promise<{{modulePascalCase}}Result> {
        let result = {{modulePascalCase}}Result(success: true, data: "Hello, \(params.message)!")
        return Promise.resolved(withResult: result)
    }
}
