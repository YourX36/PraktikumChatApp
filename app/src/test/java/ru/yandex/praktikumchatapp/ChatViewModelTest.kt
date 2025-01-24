import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update messages with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")
        val expectedMessage = Message.MyMessage(message.text)

        viewModel.sendMyMessage(message.text)

        testDispatcher.scheduler.advanceUntilIdle()

        val actualMessages = viewModel.messages.value.messages

        assertTrue(actualMessages.contains(expectedMessage))
        assertEquals(1, actualMessages.size)

    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        val jobs = mutableListOf<Job>()

        coroutineScope {
            for (message in messagesToSend) {
                jobs += launch {
                    viewModel.sendMyMessage(message.text)
                }
            }
        }

        jobs.joinAll()

        val actualMessages = viewModel.messages.value.messages
        assertEquals(100, actualMessages.size)
        assertTrue(messagesToSend.all { it in actualMessages })
    }
}