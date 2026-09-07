package hardcorequesting.commands;

import hardcorequesting.Lang;
import hardcorequesting.QuestingData;
import hardcorequesting.Translator;
import hardcorequesting.network.DataWriter;
import hardcorequesting.quests.Quest;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class CommandConvert extends CommandBase {
    public CommandConvert() {
        super("convert");
        permissionLevel = 0;
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments) {
        if (arguments.length < 1) {
            sender.addChatMessage(new ChatComponentText(Translator.translate(Lang.CONVERT_USAGE)));
            return;
        }

        String charsetName = arguments[0];
        Charset charset;
        try {
            charset = Charset.forName(charsetName);
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(Translator.translate(Lang.CONVERT_UNSUPPORTED, charsetName)));
            return;
        }

        try {
            DataWriter dw = new DataWriter();
            dw.setUseCharset(charset);
            dw.writeByte(QuestingData.FILE_VERSION.ordinal());
            Quest.FILE_HELPER.write(dw);

            byte[] bytes = dw.getBytes();
            File output = new File(Quest.questFile.getAbsolutePath() + "-" + charsetName);
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(bytes);
            fos.close();

            sender.addChatMessage(new ChatComponentText(Translator.translate(Lang.CONVERT_SUCCESS, charsetName, output.getName())));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(Translator.translate(Lang.CONVERT_ERROR, e.getMessage())));
        }
    }
}
