/*
 * Copyright 2013 ait.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.scape_project.pt.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class PipedArgsParserTest {
    
    private static Log LOG = LogFactory.getLog(PipedArgsParserTest.class);

    @Test
    public void testParse() throws IOException {
        LOG.info("TEST parse");
        CmdLineParser parser = new PipedArgsParser();

        LOG.info("TEST good input, dashes in keys");
        String strCmdLine = "a-tool a-action --input-one=\"bla\" --input-two=\"5\"";
        parser.parse(strCmdLine);

        Command command1 = new Command();
        command1.action = "a-action";
        command1.tool = "a-tool";
        command1.pairs = new HashMap<String, String>() {{
            put("input-one", "bla");
            put("input-two", "5");
        }};

        Command[] commandsExp = {
            command1
        };

        assertArrayEquals(commandsExp, parser.getCommands());

        LOG.info("TEST good input");
        strCmdLine = "\"hdfs:///file.in\" > a-tool a-action --input1=\"bla\" --input3=\"5\" > \"file with spaces.out\"";
        parser.parse(strCmdLine);

        command1.pairs = new HashMap<String, String>() {{
            put("input1", "bla");
            put("input3", "5");
        }};

        assertArrayEquals(commandsExp, parser.getCommands());
        assertEquals("hdfs:///file.in", parser.getStdinFile() );
        assertEquals("file with spaces.out", parser.getStdoutFile() );


        LOG.info("TEST good input, stdin only");
        strCmdLine = "file.in > a-tool a-action --input1=\"bla\" --input3=\"5\"";
        parser.parse(strCmdLine);

        assertEquals("file.in", parser.getStdinFile() );
        assertEquals(null, parser.getStdoutFile() );
        assertArrayEquals(commandsExp, parser.getCommands());

        LOG.info("TEST good input, stdout only");
        strCmdLine = "a-tool a-action --input1=\"bla\" --input3=\"5\" > file.out";
        parser.parse(strCmdLine);

        assertEquals(null, parser.getStdinFile() );
        assertEquals("file.out", parser.getStdoutFile() );
        assertArrayEquals(commandsExp, parser.getCommands());

        LOG.info("TEST empty input");
        strCmdLine = "a-tool a-action --input1=\"bla\" --input3=\"\"";
        parser.parse(strCmdLine);

        command1.pairs = new HashMap<String, String>() {{
            put("input1", "bla");
            put("input3", "");
        }};

        assertArrayEquals(commandsExp, parser.getCommands());

        LOG.info("TEST good input without pairs");
        strCmdLine = "a-tool a-action";
        parser.parse(strCmdLine);

        command1.pairs = new HashMap<String, String>();

        assertArrayEquals(commandsExp, parser.getCommands());

        LOG.info("TEST good input with quotes");
        strCmdLine = "a-tool a-action --input1=\"bla \\\"quoted\\\" bla\"";
        parser.parse(strCmdLine);

        command1.pairs = new HashMap<String, String>() {{
            put("input1", "bla \"quoted\" bla");
        }};
        assertArrayEquals(commandsExp, parser.getCommands());

        LOG.info("TEST good input with pipes");
        strCmdLine = "a-tool a-action --input1=\"bla \\\"quoted\\\" bla\"";
        strCmdLine += " | b-tool b-action --input2=\"test\"";
        parser.parse(strCmdLine);

        Command command2 = new Command();
        command2.tool = "b-tool";
        command2.action = "b-action";
        command2.pairs = new HashMap<String, String>() {{
            put("input2", "test");
        }};

        Command[] twoCommandsExp = {
            command1, command2
        };
        assertArrayEquals(twoCommandsExp, parser.getCommands());

        LOG.info("TEST good input with more pipes");
        strCmdLine = "a-tool a-action --input1=\"bla \\\"quoted\\\" bla\"";
        strCmdLine += " | b-tool b-action --input2=\"test\"";
        strCmdLine += " | c-tool c-action --input3=\"bla\"";
        parser.parse(strCmdLine);

        Command command3 = new Command();
        command3.tool = "c-tool";
        command3.action = "c-action";
        command3.pairs = new HashMap<String, String>() {{
            put("input3", "bla");
        }};

        Command[] moreCommandsExp = {
            command1, command2, command3
        };
        assertArrayEquals(moreCommandsExp, parser.getCommands());

        LOG.info("TEST good input with more pipes and stdin/out");
        strCmdLine = "file.in > a-tool a-action --input1=\"bla \\\"quoted\\\" bla\"";
        strCmdLine += " | b-tool b-action --input2=\"test\"";
        strCmdLine += " | c-tool c-action --input3=\"bla\" > file.out";
        parser.parse(strCmdLine);

        assertArrayEquals(moreCommandsExp, parser.getCommands());
        assertEquals("file.in", parser.getStdinFile());
        assertEquals("file.out", parser.getStdoutFile());

        LOG.info("TEST bad input, missing '='");
        strCmdLine = "a-tool a-action --input1=\"bla\" --input3 \"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST other bad input");
        strCmdLine = "--input1=\"bla\" --input3 \"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST more bad input, missing '-'");
        strCmdLine = "a-tool a-action -input1=\"bla\" --input3=\"5\"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST even more bad input");
        strCmdLine = "a-tool a-action --input1=\"bla\" -input3=\"5\"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST bad stdin");
        strCmdLine = "> a-tool a-action --input1=\"bla\" --input3=\"5\"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST bad stdout");
        strCmdLine = "a-tool a-action --input1=\"bla\" --input3=\"5\" >";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST missing action");
        strCmdLine = "a-tool --input1=\"bla\" --input3=\"5\"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST another missing action");
        strCmdLine = "file.in > a-tool --input1=\"bla\" --input3=\"5\"";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST missing command");
        strCmdLine = "file.in > > file.out";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

        LOG.info("TEST missing all");
        strCmdLine = "";

        try {
            parser.parse(strCmdLine);
            fail("no exception expected");
        }catch(IOException ex ) {
            LOG.info("expected exception: " + ex);
        }

    }

}
