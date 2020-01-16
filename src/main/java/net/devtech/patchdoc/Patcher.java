package net.devtech.patchdoc;

import com.google.gson.Gson;
import net.devtech.patchdoc.files.FileInfo;
import net.devtech.patchdoc.gui.PatchGUI;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Patcher {
	private static final Gson GSON = new Gson();
	public static Map<String, FileInfo> infos = new HashMap<>();

	public static void main(String[] args) throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		File fileData = new File(args[0]);
		File root = new File(args[1]);
		if (fileData.exists()) {
			FileInfo[] file = GSON.fromJson(new FileReader(fileData), FileInfo[].class);
			if (file != null) for (FileInfo info : file) {
				infos.put(info.getFile(), info);
			}
		}

		JFrame frame = new JFrame("Patch manager 1.0");
		frame.add(new PatchGUI(root));
		frame.setSize(500, 500);
		frame.setPreferredSize(new Dimension(200, 200));
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				System.out.println(Arrays.toString(infos.values().toArray()));
				FileWriter writer = new FileWriter(fileData);
				GSON.toJson(infos.values().toArray(new FileInfo[0]), writer);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));

	}

}
