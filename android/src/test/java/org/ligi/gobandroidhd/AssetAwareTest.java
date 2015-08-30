package org.ligi.gobandroidhd;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.ligi.axt.AXT;

public class AssetAwareTest {

    protected String readAsset(String file) throws IOException, URISyntaxException {
        return AXT.at(new File(getClass().getClassLoader().getResource(file).toURI())).readToString();
    }

}