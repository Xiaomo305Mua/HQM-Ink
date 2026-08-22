package hardcorequesting.commands;

import hardcorequesting.QuestingData;
import hardcorequesting.Translator;
import hardcorequesting.network.DataBitHelper;
import hardcorequesting.network.DataWriter;
import hardcorequesting.quests.Quest;
import hardcorequesting.quests.QuestLine;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.*;
import java.nio.charset.Charset;

public class CommandConvert extends CommandBase {
    public CommandConvert() {
        super("convert");
        permissionLevel = 0;
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments) {
        if (arguments.length < 1) {
            sendChat(sender, "hqm.command.convert.usage");
            return;
        }

        String charsetName = arguments[0];
        Charset charset;
        try {
            charset = Charset.forName(charsetName);
        } catch (Exception e) {
            sendChat(sender, "hqm.command.convert.unsupported", charsetName);
            return;
        }

        try {
            String path = QuestLine.getActiveQuestLine().mainPath;

            String lockCode = "";
            File lock = new File(path + "lock.txt");
            if (lock.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(lock));
                String line = br.readLine();
                br.close();
                if (line != null) {
                    lockCode = line.substring(0, Math.min(DataBitHelper.PASS_CODE.getMaximum(), line.length()));
                }
            }

            DataWriter dw = new DataWriter();
            dw.setUseCharset(charset);
            dw.writeByte(QuestingData.FILE_VERSION.ordinal());
            dw.writeString(lockCode, DataBitHelper.PASS_CODE);
            Quest.saveAll(dw);

            byte[] bytes = dw.getBytes();
            String outputName = "quests-" + charsetName + ".hqm";
            File output = new File(path + outputName);
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(bytes);
            fos.close();

            sendChat(sender, "hqm.command.convert.success", outputName);
        } catch (Exception e) {
            sendChat(sender, "hqm.command.convert.error", e.getMessage());
        }
    }
}