package dtd.PHS.YourExchangeRates;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;

abstract public class MyPreference {

	/**
	 *	Provides main currency (e.g: VND,EUR,USD ...), which is set 
	 *	by the first time start-up or in the Menu > Preferences 
	 * @return
	 */
	static public String getMainCurrency(Context context ) {
		String mainCurrency = getPreference(context, context.getString(R.string.PREF_MainCurrency));
		if (mainCurrency == null) mainCurrency = context.getString(R.string.DefaultMainCurrency);
		return mainCurrency;
	}

	/**
	 * Set preference prefName with data 
	 * @param prefName preference name
	 * @param data data to be stored
	 */
	public static void setPreference(Context context,String prefName, String data) {
		SharedPreferences settings = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(prefName, data);
		editor.commit();
	}

	/**
	 * Get the preference data
	 * @param context
	 * @param prefName preference name
	 * @return data stored in preference, or null if data is not found
	 */
	public static String getPreference(Context context,String prefName) {
		SharedPreferences settings = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		String prefData = settings.getString(prefName, null);	
		return prefData;		
	}

	/**
	 * Store the last online date
	 * @param context
	 * @param aDate
	 */
	public static void setLastOnlineDate(Context context, String aDate) {
		setPreference(
				context,
				context.getString(R.string.PREF_LastOnlineDate),
				aDate);
	}

	/**
	 * Return the last online date with the format dd/MM/yyyy - if no date is stored, return null
	 * @param context
	 * @return
	 */
	public static String getLastOnlineDate(Context context) {
		return getPreference(context, context.getString(R.string.PREF_LastOnlineDate));
	}

	/**
	 * Get the decimal precision of the rate shown in activity ListRates
	 * @return
	 */
	public static int getDecimalPrecision(Context context) {
		String strPrec = getPreference(context, context.getString(R.string.PREF_DecimalPrecesion));
		if ( strPrec == null ) {
			strPrec = context.getString(R.string.DefaultPrecision);
			setPreference(context, context.getString(R.string.PREF_DecimalPrecesion), strPrec);
		}
		return Integer.parseInt(strPrec);

	}

	public static void setDecimalPrecision(Context context,int prec) {
		setPreference(context, context.getString(R.string.PREF_DecimalPrecesion), Integer.toString(prec));
	}

	public static void setFirstTimeRunning(Context context,
			String data) {
		setPreference(context, context.getString(R.string.PREF_FirstTimeRunning), data);
	}

	public static String getFirstTimeRunning(Context context) {
		return getPreference(context, context.getString(R.string.PREF_FirstTimeRunning));			
	}

	public static void setMainCurrency(
			Context context,String mainCurrency) {
		setPreference(context, context.getString(R.string.PREF_MainCurrency), mainCurrency);
		setCurrenciesToShow(context, getCurrenciesToShow(context));
	}

	public static long getNumDaysToUpdate(Context context) {
		String daysStr = getPreference(context, context.getString(R.string.PREF_NumDaysToUpdate));
		if ( daysStr == null ) daysStr = context.getString(R.string.Default_NumDaysToUpdate);
		return Integer.parseInt(daysStr);
	}

	/**
	 * Return the currencies to show, default (without saved data): all currencies available
	 * @return
	 */
	public static String[] getCurrenciesToShow(Context context) {
		//TODO: implement save preference "PREF_CurrenciesToShow" by a string, each currency is separated by ","
		String currenciesToShow = getPreference(context, context.getString(R.string.PREF_CurrenciesToShow));

		//Default: no preference about currencies to show is saved yet, return all currencies
		if ( currenciesToShow == null ) {
			return MyUtility.startupCurrenciesList;
		} else {
			String[] currencies = MyUtility.splitToWords( currenciesToShow, ",");
			return currencies;
		}
	}
	
	/**
	 * Return the currencies to show exception the main currencies, 
	 * default (without saved data): all currencies available except the main ones
	 * 
	 * @return
	 */
	public static String[] getCurrenciesExcept(Context context,String mainCurrency) {
		String[] currencies = MyPreference.getCurrenciesToShow(context);
		ArrayList<String> currenciesExceptMain = new ArrayList<String>();
		for(String currency : currencies) {
			if ( currency.equals(mainCurrency)) continue;
			currenciesExceptMain.add(currency);
		}
		
		String[] ret = new String[currenciesExceptMain.size()];
		currenciesExceptMain.toArray(ret);
		
		return ret;
	}

	/**
	 * Save the preference PREF_CurrenciesToShow
	 * The main currency is always shown
	 * @param context
	 * @param currencies
	 */
	public static void setCurrenciesToShow(Context context,String[] currencies) {
		
		boolean mainCurrencyInList = false;
		String mainCurrency = MyPreference.getMainCurrency(context);
		for(String currency: currencies) {
			if ( currency.trim().equals(mainCurrency)) mainCurrencyInList = true;			
		}
		
		String[] newCurrencies;
		if ( !mainCurrencyInList ) {
			newCurrencies = MyUtility.getCurrenciesListPlus(mainCurrency,currencies);
		} else newCurrencies = currencies;
		
		String data="";
		for(String currency : newCurrencies)  {
			data = data + currency + ",";
		}
		//remove the last commas
		data = data.substring(0,data.length()-1);
		
		setPreference(context, context.getString(R.string.PREF_CurrenciesToShow), data);
	}

	/**
	 * Remove a currency from the preference "PREF_CurrenciesToShow"
	 * @param currency
	 */
	public static void removeCurrency(Context context,String currencyToRemove) {
		String[] currenciesDisplaying = getCurrenciesToShow(context);
		ArrayList<String> currenciesToShow = new ArrayList<String>();
		for(String currency : currenciesDisplaying) {
			if ( currency.equals(currencyToRemove)) continue;
			currenciesToShow.add(currency);
		}
		String[] result = new String[ currenciesToShow.size()];
		currenciesToShow.toArray(result);
		MyPreference.setCurrenciesToShow(context, result);
	}

	public static String[] getFullCurrenciesList(FirstScreen firstScreen) {
		return MyUtility.fullCurrenciesList;
	}


}
