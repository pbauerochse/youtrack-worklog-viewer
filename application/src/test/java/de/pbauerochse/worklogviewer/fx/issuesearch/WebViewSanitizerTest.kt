package de.pbauerochse.worklogviewer.fx.issuesearch

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WebViewSanitizerTest {

    @Test
    fun `markdown test with links`() {
        val input = """<div class="wiki text common-markdown"><p><strong>Test steps:</strong></p>
<ul class="wiki-list0">
<li>Do some stuff here</li>
<li>do some stuff there</li>
<li>Even more stuff</li>
</ul>
<p><strong>expected behaviour:</strong><br/>the &quot;thing&quot; behaves as &quot;expected&quot;, the expected output matches the specifications</p>
<p><strong>observed behaviour:</strong><br/>it does not behave as expected <a>Some anchor here</a></p>
<p><strong>Comments:</strong><br/><a href="http://somewebpage.tld/the/spec.pdf" target="_blank" rel="noopener noreferrer">http://somewebpage.tld/the/spec.pdf</a>, also see <a href="/issue/ISSUE-1234" class="issue-resolved" target="_self" data-issue-id="11-111" title="Referenced Ticket ISSUE-1234">ISSUE-1234</a></p>
</div>""".trimIndent()

        val expectedOutput = """<div class="wiki text common-markdown"><p><strong>Test steps:</strong></p>
<ul class="wiki-list0">
<li>Do some stuff here</li>
<li>do some stuff there</li>
<li>Even more stuff</li>
</ul>
<p><strong>expected behaviour:</strong><br/>the &quot;thing&quot; behaves as &quot;expected&quot;, the expected output matches the specifications</p>
<p><strong>observed behaviour:</strong><br/>it does not behave as expected <span class="link">Some anchor here</span></p>
<p><strong>Comments:</strong><br/><span class="link">http://somewebpage.tld/the/spec.pdf</span>, also see <span class="link issue-resolved" title="Referenced Ticket ISSUE-1234">ISSUE-1234</span></p>
</div>""".trimIndent()

        val output = WebViewSanitizer.sanitize(input)

        assertEquals(expectedOutput, output)
    }


}