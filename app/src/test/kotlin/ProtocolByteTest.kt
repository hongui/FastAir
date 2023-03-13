
import com.google.common.truth.Truth
import com.mob.lee.fastair.io.ProtocolByte
import com.mob.lee.fastair.io.ProtocolType
import org.junit.Test

class ProtocolByteTest {

    @Test
    fun protocolType(){
        Truth.assertThat(ProtocolByte.long(9527).getType()).isEqualTo(ProtocolType.L)
        Truth.assertThat(ProtocolByte.string("9527").getType()).isEqualTo(ProtocolType.W)
        Truth.assertThat(ProtocolByte.empty().getType()).isEqualTo(ProtocolType.E)
        Truth.assertThat(ProtocolByte.wrap("9527".toByteArray(),ProtocolType.B).getType()).isEqualTo(ProtocolType.B)
    }

    @Test
    fun protocolValue(){
        Truth.assertThat(ProtocolByte.long(9527).getLong()).isEqualTo(9527)
        Truth.assertThat(ProtocolByte.string("9527").getString()).isEqualTo("9527")
        Truth.assertThat(ProtocolByte.empty().getString()).isEqualTo("")
        Truth.assertThat(ProtocolByte.wrap("9527".toByteArray(),ProtocolType.B).getString()).isEqualTo("9527")
        val value="122i86o8-!&&*^&%\$^8~`\";''"
        Truth.assertThat(ProtocolByte.wrap(value.toByteArray(),ProtocolType.B).getString()).isEqualTo(value)
    }

    @Test
    fun protocolLength(){
        Truth.assertThat(ProtocolByte.long(9527).getContentLength()).isEqualTo(8)
        Truth.assertThat(ProtocolByte.string("9527").getContentLength()).isEqualTo(4)
        Truth.assertThat(ProtocolByte.empty().getContentLength()).isEqualTo(0)
        Truth.assertThat(ProtocolByte.wrap("9527".toByteArray(),ProtocolType.B).getContentLength()).isEqualTo(4)
        val value="122i86o8-!&&*^&%\$^8~`\";''"
        Truth.assertThat(ProtocolByte.wrap(value.toByteArray(),ProtocolType.B).getContentLength()).isEqualTo(value.length)
    }
}