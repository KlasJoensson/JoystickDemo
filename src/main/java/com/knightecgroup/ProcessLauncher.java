package com.knightecgroup;

import java.io.IOException;
import java.util.List;

interface ProcessLauncher {

    Process launch(List<String> command) throws IOException;
}
