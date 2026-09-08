use serde_json::{json, Map, Value};
use std::fmt;
use wasm_bindgen::{JsCast, JsValue};

#[derive(Clone, Copy, Debug)]
pub enum ErrorCode {
    NotInitialized,
    InvalidDbName,
    DatabaseError,
}

impl ErrorCode {
    fn as_str(self) -> &'static str {
        match self {
            Self::NotInitialized => "NOT_INITIALIZED",
            Self::InvalidDbName => "INVALID_DB_NAME",
            Self::DatabaseError => "DATABASE_ERROR",
        }
    }
}

#[derive(Debug)]
pub struct RuntimeError {
    code: ErrorCode,
    params: Value,
    detail: Option<String>,
}

impl RuntimeError {
    pub fn new(code: ErrorCode) -> Self {
        Self {
            code,
            params: Value::Object(Map::new()),
            detail: None,
        }
    }

    pub fn with(code: ErrorCode, params: Value) -> Self {
        Self {
            code,
            params,
            detail: None,
        }
    }

    pub fn detail(mut self, detail: impl fmt::Display) -> Self {
        self.detail = Some(detail.to_string());
        self
    }

    pub fn to_json(&self) -> Value {
        json!({
            "code": self.code.as_str(),
            "params": self.params,
            "detail": self.detail,
        })
    }
}

impl fmt::Display for RuntimeError {
    fn fmt(&self, formatter: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(formatter, "{} {}", self.code.as_str(), self.params)?;
        if let Some(detail) = &self.detail {
            write!(formatter, " :: {detail}")?;
        }
        Ok(())
    }
}

impl std::error::Error for RuntimeError {}

impl From<rusqlite::Error> for RuntimeError {
    fn from(error: rusqlite::Error) -> Self {
        Self::with(ErrorCode::DatabaseError, json!({ "operation": "sqlite" })).detail(error)
    }
}

impl From<RuntimeError> for JsValue {
    fn from(error: RuntimeError) -> Self {
        let js_error = js_sys::Error::new(error.code.as_str());
        let object: &js_sys::Object = js_error.as_ref();
        let _ = js_sys::Reflect::set(
            object,
            &JsValue::from_str("code"),
            &JsValue::from_str(error.code.as_str()),
        );
        let _ = js_sys::Reflect::set(
            object,
            &JsValue::from_str("params"),
            &JsValue::from_str(&error.params.to_string()),
        );
        if let Some(detail) = error.detail {
            let _ = js_sys::Reflect::set(
                object,
                &JsValue::from_str("detail"),
                &JsValue::from_str(&detail),
            );
        }
        js_error.unchecked_into()
    }
}

pub type Result<T> = std::result::Result<T, RuntimeError>;
