import csstype.px
import csstype.rgb
import react.FC
import react.Props
import emotion.react.css
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.useState

external interface WelcomeProps : Props {
    var name: String
}

val Welcome = FC<WelcomeProps> { props ->
    var name by useState(props.name)
    div {
        css {
            padding = 5.px
            backgroundColor = rgb(20, 40, 120)
            color = rgb(200, 200, 200)
        }
        +"hi, $name"
    }
    input {
        css {
            marginTop = 5.px
            marginBottom = 5.px
            fontSize = 14.px
        }
        type = InputType.text
        value = name
        onChange = { event ->
            name = event.target.value
        }
    }

}