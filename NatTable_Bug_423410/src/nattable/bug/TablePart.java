package nattable.bug;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultBodyDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;
import org.eclipse.swt.widgets.Composite;

public class TablePart {
	
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	private NatTable table;
	
	public static class Model {
		private String column1;
		private String column2;
		private String column3;
		private String column4;
		private String column5;
		public Model(String column1, String column2, String column3,
				String column4, String column5) {
			super();
			this.column1 = column1;
			this.column2 = column2;
			this.column3 = column3;
			this.column4 = column4;
			this.column5 = column5;
		}
		public String getColumn1() {
			return column1;
		}
		public void setColumn1(String column1) {
			this.column1 = column1;
		}
		public String getColumn2() {
			return column2;
		}
		public void setColumn2(String column2) {
			this.column2 = column2;
		}
		public String getColumn3() {
			return column3;
		}
		public void setColumn3(String column3) {
			this.column3 = column3;
		}
		public String getColumn4() {
			return column4;
		}
		public void setColumn4(String column4) {
			this.column4 = column4;
		}
		public String getColumn5() {
			return column5;
		}
		public void setColumn5(String column5) {
			this.column5 = column5;
		}
	}
	
	private List<Model> createData() {
		List<Model> data = new ArrayList<>(1000);
		for (int i = 0; i < 1000; i++) {
			data.add(new Model(
					String.valueOf(RANDOM.nextInt(1000000)), 
					String.valueOf(RANDOM.nextInt(1000000)), 
					String.valueOf(RANDOM.nextInt(1000000)),
					String.valueOf(RANDOM.nextInt(1000000)),
					String.valueOf(RANDOM.nextInt(1000000))));
		}
		return data;
	}
	
	@PostConstruct
	public void createUI(Composite parent) {

		List<Model> data = createData();
		String[] columns = new String[] {
				"column1",
				"column2",
				"column3",
				"column4",
				"column5",
		};
		DefaultBodyDataProvider<Model> bodyDataProvider = new DefaultBodyDataProvider<>(data, columns);
		
		DefaultColumnHeaderDataProvider colHeaderDataProvider = new DefaultColumnHeaderDataProvider(columns);
 		
		DataLayer dataLayer = new DataLayer(bodyDataProvider);
		
		DefaultBodyLayerStack bodyLayerStack = new DefaultBodyLayerStack(dataLayer);
		ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(
				new DefaultColumnHeaderDataLayer(colHeaderDataProvider),
				bodyLayerStack, bodyLayerStack.getSelectionLayer());
		
		CompositeLayer tableLayer = new CompositeLayer(1, 2);
		tableLayer.setChildLayer(GridRegion.COLUMN_HEADER, colHeaderLayer, 0, 0);
		tableLayer.setChildLayer(GridRegion.BODY, bodyLayerStack, 0, 1);
		
		table = new NatTable(parent, tableLayer, false);
		table.addConfiguration(new DefaultNatTableStyleConfiguration());
		table.configure();
		
		Properties props = loadState();
		if (props != null)
			table.loadState("table", props);
	}
	
	
	
	private Properties loadState() {
		try {
			File f = new File(System.getProperty("user.home"), ".nattable/table.properties");
			if (!f.exists())
				return null;
			
			Properties properties = new Properties();
			properties.load(new FileReader(f));
			return properties;
		} catch (IOException ignored) {
			return null;
		}
	}
	
	@PreDestroy
	public void saveState() {
		try {
			File f = new File(System.getProperty("user.home"), ".nattable/table.properties");
			f.getParentFile().mkdirs();
			f.createNewFile();
			
			Properties properties = new Properties();
			table.saveState("table", properties);
			properties.store(new FileWriter(f), "");
		} catch (IOException ignored) {}
	}
}
