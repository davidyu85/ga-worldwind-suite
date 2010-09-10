package au.gov.ga.worldwind.animator.panels.layerpalette;

import static au.gov.ga.worldwind.animator.util.message.AnimationMessageConstants.getAddLayerToAnimationLabelKey;
import static au.gov.ga.worldwind.animator.util.message.AnimationMessageConstants.getLayerPalettePanelNameKey;
import static au.gov.ga.worldwind.common.util.message.MessageSourceAccessor.getMessage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.gov.ga.worldwind.animator.animation.Animation;
import au.gov.ga.worldwind.animator.animation.layer.LayerIdentifier;
import au.gov.ga.worldwind.animator.application.settings.Settings;
import au.gov.ga.worldwind.animator.panels.CollapsiblePanelBase;
import au.gov.ga.worldwind.animator.util.Icons;
import au.gov.ga.worldwind.animator.util.Validate;
import au.gov.ga.worldwind.common.ui.BasicAction;

/**
 * A panel that allows the user to locate and select a layer for inclusion
 * in the animation.
 * 
 * @author James Navin (james.navin@ga.gov.au)
 */
public class LayerPalettePanel extends CollapsiblePanelBase 
{
	private static final long serialVersionUID = 20100910L;

	/** The current animation */
	private Animation animation;
	
	/** A toolbar holding operations to perform on the layers */
	private JToolBar toolbar;
	
	/** A scrollable container for holding the tree */
	private JScrollPane scrollPane;
	
	/** The list that allows the user to browse through known layers */
	private JList layerList;
	
	/** The identities of known layers */
	private ListBackedModel<LayerIdentifier> knownLayers = new ListBackedModel<LayerIdentifier>();
	
	// Actions
	private BasicAction addLayerToAnimationAction;
	
	public LayerPalettePanel(Animation animation)
	{
		Validate.notNull(animation, "An animation is required");
		this.animation = animation;
		
		setName(getMessage(getLayerPalettePanelNameKey()));
		
		initialiseLayerList();
		updateListModel();
		initialiseActions();
		initialiseToolbar();
		packComponents();
	}
	
	private void initialiseActions()
	{
		addLayerToAnimationAction = new BasicAction(getMessage(getAddLayerToAnimationLabelKey()), Icons.add.getIcon());
		addLayerToAnimationAction.setEnabled(false);
		addLayerToAnimationAction.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
				animation.addLayer((LayerIdentifier)layerList.getSelectedValue());
				layerList.repaint();
			}
		});
	}
	
	private void initialiseToolbar()
	{
		toolbar = new JToolBar();
		
		toolbar.add(addLayerToAnimationAction);
	}

	private void updateListModel()
	{
		List<LayerIdentifier> knownLayerLocations = Settings.get().getKnownLayers();
		for (LayerIdentifier layerIdentifier : knownLayerLocations)
		{
			if (knownLayers.contains(layerIdentifier))
			{
				continue;
			}
			knownLayers.add(layerIdentifier);
		}
	}

	private void initialiseLayerList()
	{
		layerList = new JList(knownLayers);
		layerList.setCellRenderer(new LayerListRenderer(animation));
		
		layerList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				LayerIdentifier layerIdentifier = (LayerIdentifier)((JList)e.getSource()).getSelectedValue();
				addLayerToAnimationAction.setEnabled(!animation.hasLayer(layerIdentifier));
			}
		});
	}

	private void packComponents()
	{
		scrollPane = new JScrollPane(layerList);
		add(toolbar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}
	
	@Override
	public void refreshView(ChangeEvent e)
	{
		if (e != null && e.getSource() instanceof Animation)
		{
			this.animation = (Animation)e.getSource();
			layerList.setCellRenderer(new LayerListRenderer(animation));
		}
		layerList.validate();
	}
	
	/**
	 * The renderer to use for the layer list. 
	 */
	private static class LayerListRenderer extends DefaultListCellRenderer
	{
		private static final Color HIGHLIGHT_COLOR = new Color(230, 247, 252);

		private static final long serialVersionUID = 20100910L;

		private Animation animation;
		
		public LayerListRenderer(Animation animation)
		{
			this.animation = animation;
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			LayerIdentifier layerIdentifier = (LayerIdentifier)value;
			
			JLabel label = (JLabel)super.getListCellRendererComponent(list, layerIdentifier, index, isSelected, cellHasFocus);
			label.setText(((LayerIdentifier)value).getName());
			
			JComponent result = new JPanel(new BorderLayout());
			result.add(label, BorderLayout.WEST);
			
			// Stripe the layer palette
			if (index % 2 == 0 && !isSelected)
			{
				label.setBackground(HIGHLIGHT_COLOR);
			} 
			result.setBackground(label.getBackground());
			
			// Add the 'included in animation' indicator
			if (animation.hasLayer(layerIdentifier))
			{
				result.add(new JLabel(Icons.flag.getIcon()), BorderLayout.EAST);
			}
			
			return result;
		}
	}
	
	/**
	 * An implementation of the {@link ListModel} interface that is backed by a {@link List}.
	 * <p/>
	 * Add and remove events are fired when elements are added or removed from the backing list.
	 */
	private static class ListBackedModel<T> extends AbstractListModel
	{
		private static final long serialVersionUID = 20100910L;
		
		private List<T> backingList = new ArrayList<T>();

		public boolean contains(T object)
		{
			return backingList.contains(object);
		}
		
		public void add(T object)
		{
			backingList.add(object);
			fireIntervalAdded(this, backingList.size() - 1, backingList.size());
		}
		
		public void remove(T object)
		{
			backingList.remove(object);
			fireIntervalRemoved(this,  backingList.size(),  backingList.size() - 1);
		}
		
		@Override
		public int getSize()
		{
			return backingList.size();
		}

		@Override
		public Object getElementAt(int index)
		{
			return backingList.get(index);
		}
		
	}
}
