import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.ButtonType
import react.dom.html.InputType
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import react.dom.onChange
import react.useState

external interface InputProps : Props {
    var onSubmit: (String,Int)->Unit
}

val InputComponent = FC<InputProps>{ props ->
    val (text, setText) = useState("")
    val (priority, setPriority) = useState(0)

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(text, priority)
        setText("")
    }

    val changeHandler: ChangeEventHandler<HTMLInputElement> = {
        setText(it.target.value)
    }

    val priorityChangeHandler: ChangeEventHandler<HTMLInputElement> = {
        if(it.target.value.all { it.isDigit() }){
            setPriority(it.target.value.toInt())
        }
    }

    form{
        onSubmit = submitHandler
        input{
            type = InputType.text
            onChange = changeHandler
            value = text
        }
        input{
            type = InputType.number
            onChange = priorityChangeHandler
            value = priority.toString()
        }
        button{
            +"+"
            type = ButtonType.submit
        }
    }
}