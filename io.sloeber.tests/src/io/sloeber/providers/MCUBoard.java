package io.sloeber.providers;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.sloeber.core.BoardAttributes;
import io.sloeber.core.Example;
import io.sloeber.core.api.BoardDescription;
import io.sloeber.core.api.BoardsManager;
import io.sloeber.core.api.Json.ArduinoPackage;
import io.sloeber.core.api.Json.ArduinoPlatform;
import io.sloeber.core.api.Json.ArduinoPlatformVersion;

@SuppressWarnings("nls")
public class MCUBoard {

    protected BoardDescription myBoardDescriptor = null;
    public BoardAttributes myAttributes = new BoardAttributes();
    public String mySlangName = "";
    public String mySerialPort = "Serial";

    @SuppressWarnings("static-method")
    public MCUBoard createMCUBoard(BoardDescription boardDesc) {
        MCUBoard ret = new MCUBoard();
        ret.myBoardDescriptor = boardDesc;
        ret.mySlangName = boardDesc.getBoardName();
        return ret;
    }

    public static List<MCUBoard> getAllBoards(String provider, MCUBoard creator) {
        List<MCUBoard> ret = new LinkedList<>();
        ArduinoPackage arduinoPkg = BoardsManager.getPackageByProvider(provider);
        for (ArduinoPlatform curPlatform : arduinoPkg.getPlatforms()) {
            ArduinoPlatformVersion curPlatformVersion = curPlatform.getNewestInstalled();
            if (curPlatformVersion != null) {
                List<BoardDescription> boardDescriptions = BoardDescription
                        .makeBoardDescriptors(curPlatformVersion.getBoardsFile());
                for (BoardDescription curBoardDesc : boardDescriptions) {
                    ret.add(creator.createMCUBoard(curBoardDesc));
                }
            }
        }
        return ret;

    }

    public BoardDescription getBoardDescriptor() {
        return myBoardDescriptor;
    }

    public boolean isExampleSupported(Example example) {
        if (myBoardDescriptor == null) {
            return false;
        }

        /*
         * There is one know Teensy example that does not
         * run on all teensy boards
         */
        if ("Teensy".equalsIgnoreCase(getID())) {
            if (example.getFQN().contains("Teensy/USB_Mouse/Buttons")) {
                String boardID = myBoardDescriptor.getBoardID();
                if ("teensypp2".equals(boardID) || "teensy2".equals(boardID)) {
                    return false;
                }
            }
        }
        myAttributes.boardName = myBoardDescriptor.getBoardID();
        return myAttributes.compatibleWithExampleRequirements(example.getRequiredBoardAttributes());
    }

    /**
     * give the name of the board as it appears in boards.txt
     * 
     * @return the name of the board as shown in the gui
     */
    public String getID() {
        if (myBoardDescriptor == null) {
            return null;
        }
        return myBoardDescriptor.getBoardID();
    }

    /**
     * give the name of the board as it is generally known
     * For instance the board "Arduino genuino uno" is uno
     * or zero programming port is zero
     *
     * @return the name of the board as commonly used
     */
    public String getSlangName() {
        if (mySlangName != null) {
            return mySlangName;
        }
        return getID();
    }

    @SuppressWarnings({ "static-method" })
    public Map<String, String> getBoardOptions(Example example) {
        Map<String, String> ret = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        return ret;
    }

}