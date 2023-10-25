import csstype.BackgroundColor
import csstype.Color
import csstype.ColorProperty
import csstype.px
import kotlinx.browser.document
import react.create
import react.dom.client.createRoot

import emotion.react.css
import kotlinx.js.Object
import kotlinx.js.jso
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.events.MouseEvent
import react.dom.html.HTMLAttributes
import react.dom.html.InputType
import react.dom.html.ReactHTML.body
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.dom.html.ReactHTML.textarea
import kotlin.js.Promise


@JsModule("axios")
@JsNonModule
external fun <T> axios(config: AxiosConfigSettings): Promise<AxiosResponse<T>>

// Type definition
external interface AxiosConfigSettings {
    var url: String
    var method: String
    var baseUrl: String
    var timeout: Number
    var data: dynamic
    var transferRequest: dynamic
    var transferResponse: dynamic
    var headers: dynamic
    var params: dynamic
    var withCredentials: Boolean
    var adapter: dynamic
    var auth: dynamic
    var responseType: String
    var xsrfCookieName: String
    var xsrfHeaderName: String
    var onUploadProgress: dynamic
    var onDownloadProgress: dynamic
    var maxContentLength: Number
    var validateStatus: (Number) -> Boolean
    var maxRedirects: Number
    var httpAgent: dynamic
    var httpsAgent: dynamic
    var proxy: dynamic
    var cancelToken: dynamic
}

external interface AxiosResponse<T> {
    val data: T
    val status: Number
    val statusText: String
    val headers: dynamic
    val config: AxiosConfigSettings
}

external interface AxiosProps : Props



enum class Theme(
    val backgroundColor: BackgroundColor,
    val color: ColorProperty,
) {
    LIGHT(Color("white"), Color("black")),
    DARK(Color("black"), Color("white")),

    ;
}



external interface TenantData {
    val name: String
    val id: Int
}


data class TenantResult(
    val name: String,
    val id: Int
)



var options: List<TenantData> = emptyList()

val ThemeContext = createContext(Theme.LIGHT)
var theme = Theme.DARK

val MainApp = FC<PropsWithChildren> { props ->

    ThemeContext(Theme.DARK) {
        div {
            ThemeContext.Consumer {
                children = { theme ->
                    div.create {
                        css {
                            color = theme.color
                            backgroundColor = theme.backgroundColor
                        }

                        +"Hello from React"
                    }
                }
            }

            css {
                width = 100.px
                height = 100.px
                backgroundColor = Color("red")
            }

            // Setting an attribute
            title = "My title"

            onClick = {

                theme = if (theme == Theme.DARK) {
                    Theme.LIGHT
                } else {
                    Theme.DARK
                }

                ThemeContext.Provider.apply { theme }
            }

            // Setting a custom attribute
            asDynamic()["my-attribute"] = 100

            // Assigning additional attributes
            Object.assign(this, jso<HTMLAttributes<HTMLDivElement>> { tabIndex = 0 })

            // Appending children from props
            +props.children



            val tenantData: () -> Unit = {
                var name = "core"
            }

            val code = "tenants"
            axios<List<TenantData>>(jso {
                url = "http://localhost:8080/api/$code"
                timeout = 5000
                method = "post"
                transferRequest = "application/json"
                data = tenantData
            }).then { response ->

                val data: List<TenantData> = response.data
                options = data
                console.log(response)

            }.catch { error ->
                console.log(error)
            }

            axios<List<TenantData>>(jso {
                url = "http://localhost:8080/api/$code"
                timeout = 5000
                method = "get"
            }).then { response ->

                    val data: List<TenantData> = response.data
                    options = data
                    console.log(response)

                }.catch { error ->
                    console.log(error)
                }

            // Form elements https://facebook.github.io/react/docs/forms.html

            input {
                defaultValue = "foo"
            }

            input {
                type = InputType.checkbox
                defaultChecked = true
            }

            textarea {
                defaultValue = "foobar"
            }

            options = useMemo { emptyList() }

            select {
                // defaultValue = ""

                options.forEach {
                    option {
                        key = it.name
                        value = it.name

                        +it.name
                    }
                }
            }

            select {
                multiple = true
                // TODO: Remove `unsafeCast` after declarations improvement
                defaultValue = arrayOf("foo", "bar").unsafeCast<String>()

                options.forEach {
                    option {
                        key = it.name
                        value = it.name

                        +it.name
                    }
                }
            }
        }
    }
}


fun main() {
    val container1 = document.createElement("div")
    val container2 = document.createElement("div")

    document.body!!.appendChild(container1)
    document.body!!.appendChild(container2)

    val welcome = Welcome.create {
        name = "some code"
    }
    val app = MainApp.create {
    }

    createRoot(container1).render(welcome)
    createRoot(container2).render(app)
}
