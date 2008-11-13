package util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Icons
{
	public static final ImageIcon remove;
	public static final ImageIcon search;
	public static final ImageIcon maximize;
	public static final ImageIcon close;
	public static final ImageIcon restore;
	public static final ImageIcon run;
	public static final ImageIcon monitor;

	static
	{
		remove = loadIcon("/data/icons/remove.gif");
		search = loadIcon("/data/icons/search.gif");
		maximize = loadIcon("/data/icons/maximize.gif");
		close = loadIcon("/data/icons/close.gif");
		restore = loadIcon("/data/icons/restore.gif");
		run = loadIcon("/data/icons/run.gif");
		monitor = loadIcon("/data/icons/monitor.gif");
	}

	private static ImageIcon loadIcon(String path)
	{
		try
		{
			return new ImageIcon(ImageIO.read(Icons.class
					.getResourceAsStream(path)));
		}
		catch (IOException e)
		{
			return null;
		}
	}
}