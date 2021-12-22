
object TUI {

    fun init(){
        HAL.init()
        SerialEmitter.init()
        LCD.init()
        KBD.init()
    }

}

fun main() {
    HAL.init()
    SerialEmitter.init()
    LCD.init()
    KBD.init()
    TUI.init()
}