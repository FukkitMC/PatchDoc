package net.devtech.patchdoc.gui;

import net.devtech.patchdoc.Patcher;
import net.devtech.patchdoc.files.FileInfo;
import net.devtech.patchdoc.files.FileStatus;
import net.devtech.patchdoc.gui.view.JIconRowHeader;
import net.devtech.utilib.functions.ThrowingConsumer;
import net.devtech.utilib.functions.ThrowingSupplier;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PatchGUI extends JPanel {
	private static final FileStatus[] STATUSES = FileStatus.values();
	private static final Icon TODO = new ImageIcon(JIconRowHeader.class.getResource("/todo.png"));
	private static final Icon COMPLETE = new ImageIcon(JIconRowHeader.class.getResource("/complete.png"));
	private static final Icon IN_PROGRESS = new ImageIcon(JIconRowHeader.class.getResource("/inprogress.png"));
	private static final Field ICON_AREA = ((ThrowingSupplier<Field>) () -> Gutter.class.getDeclaredField("iconArea")).get();

	public PatchGUI(File folder) throws IllegalAccessException {
		RSyntaxTextArea patch = new RSyntaxTextArea();
		patch.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		patch.setEditable(false);
		ICON_AREA.setAccessible(true);
		RTextScrollPane sp = new RTextScrollPane(patch);
		JIconRowHeader header = new JIconRowHeader(patch);
		ICON_AREA.set(sp.getGutter(), header);
		sp.setIconRowHeaderEnabled(true);
		JTree tree = new JTree(new FileSystemModel(folder));
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				String relative = folder.toURI().relativize(((File)value).toURI()).getPath();
				FileInfo info = Patcher.infos.get(relative);
				if(info == null) {
					System.err.println("no info found for file: " + relative);
					info = new FileInfo(FileStatus.TODO, relative, new HashMap<>());
					Patcher.infos.put(relative, info);
				}

				switch (info.status) {
					case TODO:
						this.setIcon(PatchGUI.TODO);
						break;
					case COMPLETE:
						this.setIcon(PatchGUI.COMPLETE);
						break;
					case IN_PROGRESS:
						this.setIcon(PatchGUI.IN_PROGRESS);
						break;
				}
				return this;
			}
		});
		tree.addTreeSelectionListener(e -> {
			File selected = (File) e.getPath().getLastPathComponent();
			String relative = folder.toURI().relativize(selected.toURI()).getPath();
			FileInfo info = Patcher.infos.get(relative);
			if(info == null) {
				System.err.println("no info found for file: " + relative);
				info = new FileInfo(FileStatus.TODO, relative, new HashMap<>());
				Patcher.infos.put(relative, info);
			}

			setText(patch, new File(folder, info.getFile()));
			header.setComments(info.getComments());
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					TreePath path = tree.getClosestPathForLocation(e.getX(), e.getY());
					File selected = (File) path.getLastPathComponent();
					String relative = folder.toURI().relativize(selected.toURI()).getPath();
					FileInfo info = Patcher.infos.get(relative);
					info.status = STATUSES[(info.status.ordinal()+1) % STATUSES.length];
					tree.repaint();
				}
			}
		});


		this.setLayout(new GridLayout(1, 2));
		this.add(sp);
		this.add(tree);
		this.setBorder(BorderFactory.createEtchedBorder());
	}

	public static void setText(RSyntaxTextArea area, File patch) {
		List<Integer> rem = new ArrayList<>();
		List<Integer> add = new ArrayList<>();
		String text;
		try (BufferedReader reader = new BufferedReader(new FileReader(patch))) {
			AtomicInteger line = new AtomicInteger();
			StringBuilder builder = new StringBuilder();
			reader.lines().forEach(s -> {
				int ln = line.getAndIncrement();
				if (s.startsWith("-")) rem.add(ln);
				else if (s.startsWith("+")) add.add(ln);
				builder.append(s).append('\n');
			});
			text = builder.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		area.setText(text);
		rem.forEach((ThrowingConsumer<Integer>) i -> area.addLineHighlight(i, new Color(255, 0, 0, 128)));
		add.forEach((ThrowingConsumer<Integer>) i -> area.addLineHighlight(i, new Color(0, 255, 0, 128)));
	}

}
