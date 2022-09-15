import net.sonmoosans.dui.Generator
import net.sonmoosans.dui.components.*
import net.sonmoosans.dui.dynamicComponent

data class PagerProps(
    var page: Int
)

private object PropsGenerator : Generator<PagerProps> {
    override fun encode(props: PagerProps): String {
        return "${props.page}"
    }

    override fun parse(data: String): PagerProps {
        return PagerProps(data.toInt())
    }
}

val PagerExample = dynamicComponent(PropsGenerator) {
    dynamic = true

    //Notice that the 'props' outside the 'setPage' lambda is not same as the one inside
    //InteractionContext::props points to the data parsed from Interaction Event
    pager(page = props.page, setPage = {this.props.page = it}) {
        page {
            embed(title = "Page 1")
        }
        page {
            embed(title = "Page 2")
        }
        page {
            embed(title = "Page 3")
        }
    }
}