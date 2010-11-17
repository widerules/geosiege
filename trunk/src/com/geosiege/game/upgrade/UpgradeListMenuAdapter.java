package com.geosiege.game.upgrade;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.geosiege.game.R;
import com.geosiege.game.ui.UpgradeBar;

/**
 * An adapter that renders a list of upgrades within the upgrades activity. 
 * Each upgrade is rendered as an upgrade row. How the row is rendered depends
 * on the type of upgrade. For example, upgrades with levels may be rendered
 * with a progress bar while upgrades that may simply be toggled on and off
 * will not.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class UpgradeListMenuAdapter extends ArrayAdapter<Upgrade> {

  private static final String BUY_EQUIPPED = "Equipped";
  private static final String BUY_UNEQUIPPED = "Off";
  private static final String BUY_AVAILABLE = "Buy";
  private static final String BUY_UPGRADE = "Upgrade";
  private static final String BUY_TOO_EXPENSIVE = "Too expensive";
  private static final String BUY_MAXED = "Max";
  private static final int ENABLED_COLOR = Color.rgb(255, 255, 255);
  private static final int DISABLED_COLOR = Color.rgb(114, 114, 114);
  
  public static DecimalFormat MONEY_FORMAT = new DecimalFormat();
  static {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setGroupingSeparator(',');
    MONEY_FORMAT.setDecimalFormatSymbols(dfs);
  }
  
  private Context context;
  private List<Upgrade> upgrades;
  private List<Runnable> listeners;
  private Upgrades upgrader;

  public UpgradeListMenuAdapter(Context context, int rowLayoutId, Upgrades upgrader, List<Upgrade> upgrades) {
    super(context, rowLayoutId, upgrades);
    this.context = context;
    this.upgrades = upgrades;
    this.upgrader = upgrader;
    listeners = new ArrayList<Runnable>();
  }
  
  public void addRefreshRequestListener(Runnable runnable) {
    listeners.add(runnable);
  }
  
  private void requestUIRefresh() {
    for(Runnable runnable : listeners) {
      runnable.run();
    }
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View v = convertView;
    if (v == null) {
      LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(R.layout.upgraderow, parent, false);
    }
    final Upgrade upgrade = upgrades.get(position);
    if (upgrade != null) {
      TextView title = (TextView) v.findViewById(R.id.upgrade_name);
      title.setText(upgrade.getName());
      
      TextView cost = (TextView) v.findViewById(R.id.upgrade_cost);
      cost.setText("- $" + MONEY_FORMAT.format(upgrade.getCost()));
      cost.setVisibility(upgrade.isBought() ? View.INVISIBLE : View.VISIBLE);

      UpgradeBar upgradeBar = (UpgradeBar) v.findViewById(R.id.upgrade_bar);
      Button buyButton = (Button) v.findViewById(R.id.upgrade_buy_button);
      
      boolean enabled = upgrade.canAfford() && upgrade.isAvailable();
      buyButton.setEnabled(enabled);
      buyButton.setTextColor(enabled ? ENABLED_COLOR : DISABLED_COLOR);

      // Render the row differently based on the type of upgrade it is
      // supposed to display.
      
      if (upgrade instanceof LeveledUpgrade) {
        renderRow((LeveledUpgrade) upgrade, upgradeBar, buyButton);
      } else if (upgrade instanceof ToggleUpgrade) {
        renderRow((ToggleUpgrade) upgrade, upgradeBar, buyButton);
      }
    }
    return v;
  }
  
  /**
   * Render a LeveledUpgrade.
   */
  private void renderRow(final LeveledUpgrade upgrade, UpgradeBar upgradeBar, Button buyButton) {
    upgradeBar.setVisibility(View.VISIBLE);
    upgradeBar.setVisibleDashes(upgrade.getMaxLevel());
    upgradeBar.setEnabledDashes(upgrade.getLevel());
    buyButton.setBackgroundResource(R.drawable.side_button);
    buyButton.setText(getBuyButtonText(upgrade));
    buyButton.setOnClickListener(new OnClickListener() {
      public void onClick(View b) {
        buyUpgrade(upgrade);
        upgrader.save();
        requestUIRefresh();
      }
    });
  }
  
  /**
   * Render a ToggleableUpgrade.
   */
  private void renderRow(final ToggleUpgrade upgrade, UpgradeBar upgradeBar, Button buyButton) {
    upgradeBar.setVisibility(View.INVISIBLE);
    buyButton.setText(getBuyButtonText(upgrade));
    buyButton.setBackgroundResource(
        upgrade.isEquipped() ?
        R.drawable.side_button_toggle_on :
        R.drawable.side_button_toggle_off);
    buyButton.setOnClickListener(new OnClickListener() {
      public void onClick(View b) {
        if (!upgrade.isBought()) {
          buyUpgrade(upgrade);
          upgrade.equip();
        } else if (!upgrade.isEquipped()){
          upgrade.equip();
        } else {
          upgrade.unequip();
        }

        upgrader.save();
        requestUIRefresh();
      }
    });
  }
  
  private void buyUpgrade(Upgrade upgrade) {
    if (upgrade.canAfford()) {
      upgrade.buy();
    }
  }
  
  private String getBuyButtonText(ToggleUpgrade upgrade) {
    
    if (upgrade.isBought() && !upgrade.isEquipped()) {
      return BUY_UNEQUIPPED;
    } else if (upgrade.isEquipped()) {
      return BUY_EQUIPPED;
    } else if (!upgrade.canAfford()){
      return BUY_TOO_EXPENSIVE;
    } else {
      return BUY_AVAILABLE;
    }
  }
  
  private String getBuyButtonText(LeveledUpgrade upgrade) {
    if (upgrade.isLevelMaxed()) {
      return BUY_MAXED;
    } else if (upgrade.canAfford()) {
      return BUY_UPGRADE;
    } else {
      return BUY_TOO_EXPENSIVE;
    }
  }
}
