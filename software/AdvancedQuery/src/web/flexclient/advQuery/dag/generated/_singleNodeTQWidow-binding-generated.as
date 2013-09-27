
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import mx.core.IPropertyChangeNotifier;
import mx.events.PropertyChangeEvent;
import mx.utils.ObjectProxy;
import mx.utils.UIDUtil;

import String;
import mx.containers.HBox;
import mx.controls.Button;
import mx.formatters.DateFormatter;
import mx.controls.DateField;
import mx.validators.StringValidator;
import int;
import Object;
import mx.controls.ComboBox;
import mx.containers.VBox;
import mx.collections.ArrayCollection;
import mx.controls.TextInput;
import mx.controls.Label;

class BindableProperty
{
	/**
	 * generated bindable wrapper for property arithmaticOpsCb (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'arithmaticOpsCb' moved to '_288606243arithmaticOpsCb'
	 */

    [Bindable(event="propertyChange")]
    public function get arithmaticOpsCb():mx.controls.ComboBox
    {
        return this._288606243arithmaticOpsCb;
    }

    public function set arithmaticOpsCb(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._288606243arithmaticOpsCb;
        if (oldValue !== value)
        {
			this._288606243arithmaticOpsCb = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "arithmaticOpsCb", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property arithmeticOpLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'arithmeticOpLabel' moved to '_455265911arithmeticOpLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get arithmeticOpLabel():mx.controls.Label
    {
        return this._455265911arithmeticOpLabel;
    }

    public function set arithmeticOpLabel(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._455265911arithmeticOpLabel;
        if (oldValue !== value)
        {
			this._455265911arithmeticOpLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "arithmeticOpLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property attributesCb (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'attributesCb' moved to '_1016547306attributesCb'
	 */

    [Bindable(event="propertyChange")]
    public function get attributesCb():mx.controls.ComboBox
    {
        return this._1016547306attributesCb;
    }

    public function set attributesCb(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._1016547306attributesCb;
        if (oldValue !== value)
        {
			this._1016547306attributesCb = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "attributesCb", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property cancelButton (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cancelButton' moved to '_1990131276cancelButton'
	 */

    [Bindable(event="propertyChange")]
    public function get cancelButton():mx.controls.Button
    {
        return this._1990131276cancelButton;
    }

    public function set cancelButton(value:mx.controls.Button):void
    {
    	var oldValue:Object = this._1990131276cancelButton;
        if (oldValue !== value)
        {
			this._1990131276cancelButton = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cancelButton", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property columnLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'columnLabel' moved to '_852860610columnLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get columnLabel():mx.controls.Label
    {
        return this._852860610columnLabel;
    }

    public function set columnLabel(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._852860610columnLabel;
        if (oldValue !== value)
        {
			this._852860610columnLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "columnLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property customColumnName (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'customColumnName' moved to '_1956137646customColumnName'
	 */

    [Bindable(event="propertyChange")]
    public function get customColumnName():mx.controls.TextInput
    {
        return this._1956137646customColumnName;
    }

    public function set customColumnName(value:mx.controls.TextInput):void
    {
    	var oldValue:Object = this._1956137646customColumnName;
        if (oldValue !== value)
        {
			this._1956137646customColumnName = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "customColumnName", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property datePicker (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'datePicker' moved to '_435546588datePicker'
	 */

    [Bindable(event="propertyChange")]
    public function get datePicker():mx.controls.DateField
    {
        return this._435546588datePicker;
    }

    public function set datePicker(value:mx.controls.DateField):void
    {
    	var oldValue:Object = this._435546588datePicker;
        if (oldValue !== value)
        {
			this._435546588datePicker = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "datePicker", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityLabel' moved to '_1263575249entityLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get entityLabel():mx.controls.Label
    {
        return this._1263575249entityLabel;
    }

    public function set entityLabel(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._1263575249entityLabel;
        if (oldValue !== value)
        {
			this._1263575249entityLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property fieldVal (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'fieldVal' moved to '_929016889fieldVal'
	 */

    [Bindable(event="propertyChange")]
    public function get fieldVal():mx.validators.StringValidator
    {
        return this._929016889fieldVal;
    }

    public function set fieldVal(value:mx.validators.StringValidator):void
    {
    	var oldValue:Object = this._929016889fieldVal;
        if (oldValue !== value)
        {
			this._929016889fieldVal = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "fieldVal", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property firstComboPlace (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'firstComboPlace' moved to '_169251561firstComboPlace'
	 */

    [Bindable(event="propertyChange")]
    public function get firstComboPlace():mx.containers.VBox
    {
        return this._169251561firstComboPlace;
    }

    public function set firstComboPlace(value:mx.containers.VBox):void
    {
    	var oldValue:Object = this._169251561firstComboPlace;
        if (oldValue !== value)
        {
			this._169251561firstComboPlace = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "firstComboPlace", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property labelHBox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'labelHBox' moved to '_608131753labelHBox'
	 */

    [Bindable(event="propertyChange")]
    public function get labelHBox():mx.containers.HBox
    {
        return this._608131753labelHBox;
    }

    public function set labelHBox(value:mx.containers.HBox):void
    {
    	var oldValue:Object = this._608131753labelHBox;
        if (oldValue !== value)
        {
			this._608131753labelHBox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "labelHBox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property lb5 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb5' moved to '_106879lb5'
	 */

    [Bindable(event="propertyChange")]
    public function get lb5():mx.controls.Label
    {
        return this._106879lb5;
    }

    public function set lb5(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106879lb5;
        if (oldValue !== value)
        {
			this._106879lb5 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb5", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property lb6 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb6' moved to '_106880lb6'
	 */

    [Bindable(event="propertyChange")]
    public function get lb6():mx.controls.Label
    {
        return this._106880lb6;
    }

    public function set lb6(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106880lb6;
        if (oldValue !== value)
        {
			this._106880lb6 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb6", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property lb7 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb7' moved to '_106881lb7'
	 */

    [Bindable(event="propertyChange")]
    public function get lb7():mx.controls.Label
    {
        return this._106881lb7;
    }

    public function set lb7(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106881lb7;
        if (oldValue !== value)
        {
			this._106881lb7 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb7", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property leftDatePicker (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'leftDatePicker' moved to '_2087930179leftDatePicker'
	 */

    [Bindable(event="propertyChange")]
    public function get leftDatePicker():mx.controls.Label
    {
        return this._2087930179leftDatePicker;
    }

    public function set leftDatePicker(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._2087930179leftDatePicker;
        if (oldValue !== value)
        {
			this._2087930179leftDatePicker = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "leftDatePicker", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myBox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myBox' moved to '_104335487myBox'
	 */

    [Bindable(event="propertyChange")]
    public function get myBox():mx.containers.HBox
    {
        return this._104335487myBox;
    }

    public function set myBox(value:mx.containers.HBox):void
    {
    	var oldValue:Object = this._104335487myBox;
        if (oldValue !== value)
        {
			this._104335487myBox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myBox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property relationalOpsCb (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'relationalOpsCb' moved to '_135252938relationalOpsCb'
	 */

    [Bindable(event="propertyChange")]
    public function get relationalOpsCb():mx.controls.ComboBox
    {
        return this._135252938relationalOpsCb;
    }

    public function set relationalOpsCb(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._135252938relationalOpsCb;
        if (oldValue !== value)
        {
			this._135252938relationalOpsCb = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "relationalOpsCb", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property relationalOpsLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'relationalOpsLabel' moved to '_649331625relationalOpsLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get relationalOpsLabel():mx.controls.Label
    {
        return this._649331625relationalOpsLabel;
    }

    public function set relationalOpsLabel(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._649331625relationalOpsLabel;
        if (oldValue !== value)
        {
			this._649331625relationalOpsLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "relationalOpsLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property submitButton1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'submitButton1' moved to '_7123961submitButton1'
	 */

    [Bindable(event="propertyChange")]
    public function get submitButton1():mx.controls.Button
    {
        return this._7123961submitButton1;
    }

    public function set submitButton1(value:mx.controls.Button):void
    {
    	var oldValue:Object = this._7123961submitButton1;
        if (oldValue !== value)
        {
			this._7123961submitButton1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "submitButton1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeFormatter (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'timeFormatter' moved to '_452832131timeFormatter'
	 */

    [Bindable(event="propertyChange")]
    public function get timeFormatter():mx.formatters.DateFormatter
    {
        return this._452832131timeFormatter;
    }

    public function set timeFormatter(value:mx.formatters.DateFormatter):void
    {
    	var oldValue:Object = this._452832131timeFormatter;
        if (oldValue !== value)
        {
			this._452832131timeFormatter = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeFormatter", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeInputBox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'timeInputBox' moved to '_813284238timeInputBox'
	 */

    [Bindable(event="propertyChange")]
    public function get timeInputBox():mx.controls.TextInput
    {
        return this._813284238timeInputBox;
    }

    public function set timeInputBox(value:mx.controls.TextInput):void
    {
    	var oldValue:Object = this._813284238timeInputBox;
        if (oldValue !== value)
        {
			this._813284238timeInputBox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeInputBox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeIntervalCb (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'timeIntervalCb' moved to '_1233560241timeIntervalCb'
	 */

    [Bindable(event="propertyChange")]
    public function get timeIntervalCb():mx.controls.ComboBox
    {
        return this._1233560241timeIntervalCb;
    }

    public function set timeIntervalCb(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._1233560241timeIntervalCb;
        if (oldValue !== value)
        {
			this._1233560241timeIntervalCb = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeIntervalCb", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeIntervalCb6 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'timeIntervalCb6' moved to '_414338139timeIntervalCb6'
	 */

    [Bindable(event="propertyChange")]
    public function get timeIntervalCb6():mx.controls.ComboBox
    {
        return this._414338139timeIntervalCb6;
    }

    public function set timeIntervalCb6(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._414338139timeIntervalCb6;
        if (oldValue !== value)
        {
			this._414338139timeIntervalCb6 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeIntervalCb6", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityLabelString (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityLabelString' moved to '_948522050entityLabelString'
	 */

    [Bindable(event="propertyChange")]
    public function get entityLabelString():String
    {
        return this._948522050entityLabelString;
    }

    public function set entityLabelString(value:String):void
    {
    	var oldValue:Object = this._948522050entityLabelString;
        if (oldValue !== value)
        {
			this._948522050entityLabelString = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityLabelString", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property buttonLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'buttonLabel' moved to '_1777527070buttonLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get buttonLabel():String
    {
        return this._1777527070buttonLabel;
    }

    public function set buttonLabel(value:String):void
    {
    	var oldValue:Object = this._1777527070buttonLabel;
        if (oldValue !== value)
        {
			this._1777527070buttonLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "buttonLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property datePickerLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'datePickerLabel' moved to '_333812104datePickerLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get datePickerLabel():String
    {
        return this._333812104datePickerLabel;
    }

    public function set datePickerLabel(value:String):void
    {
    	var oldValue:Object = this._333812104datePickerLabel;
        if (oldValue !== value)
        {
			this._333812104datePickerLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "datePickerLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityName (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityName' moved to '_1483200242entityName'
	 */

    [Bindable(event="propertyChange")]
    public function get entityName():String
    {
        return this._1483200242entityName;
    }

    public function set entityName(value:String):void
    {
    	var oldValue:Object = this._1483200242entityName;
        if (oldValue !== value)
        {
			this._1483200242entityName = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityName", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityExpressionId (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityExpressionId' moved to '_1438854826entityExpressionId'
	 */

    [Bindable(event="propertyChange")]
    public function get entityExpressionId():int
    {
        return this._1438854826entityExpressionId;
    }

    public function set entityExpressionId(value:int):void
    {
    	var oldValue:Object = this._1438854826entityExpressionId;
        if (oldValue !== value)
        {
			this._1438854826entityExpressionId = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityExpressionId", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property customNodeName (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'customNodeName' moved to '_1697072578customNodeName'
	 */

    [Bindable(event="propertyChange")]
    public function get customNodeName():String
    {
        return this._1697072578customNodeName;
    }

    public function set customNodeName(value:String):void
    {
    	var oldValue:Object = this._1697072578customNodeName;
        if (oldValue !== value)
        {
			this._1697072578customNodeName = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "customNodeName", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property arithmeticOpDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'arithmeticOpDp' moved to '_1992845847arithmeticOpDp'
	 */

    [Bindable(event="propertyChange")]
    public function get arithmeticOpDp():ArrayCollection
    {
        return this._1992845847arithmeticOpDp;
    }

    public function set arithmeticOpDp(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1992845847arithmeticOpDp;
        if (oldValue !== value)
        {
			this._1992845847arithmeticOpDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "arithmeticOpDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property attributesDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'attributesDp' moved to '_1016547261attributesDp'
	 */

    [Bindable(event="propertyChange")]
    public function get attributesDp():ArrayCollection
    {
        return this._1016547261attributesDp;
    }

    public function set attributesDp(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1016547261attributesDp;
        if (oldValue !== value)
        {
			this._1016547261attributesDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "attributesDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property relationalDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'relationalDp' moved to '_262385133relationalDp'
	 */

    [Bindable(event="propertyChange")]
    public function get relationalDp():ArrayCollection
    {
        return this._262385133relationalDp;
    }

    public function set relationalDp(value:ArrayCollection):void
    {
    	var oldValue:Object = this._262385133relationalDp;
        if (oldValue !== value)
        {
			this._262385133relationalDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "relationalDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeIntervalsDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'timeIntervalsDp' moved to '_414292883timeIntervalsDp'
	 */

    [Bindable(event="propertyChange")]
    public function get timeIntervalsDp():ArrayCollection
    {
        return this._414292883timeIntervalsDp;
    }

    public function set timeIntervalsDp(value:ArrayCollection):void
    {
    	var oldValue:Object = this._414292883timeIntervalsDp;
        if (oldValue !== value)
        {
			this._414292883timeIntervalsDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeIntervalsDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityDp' moved to '_2102100017entityDp'
	 */

    [Bindable(event="propertyChange")]
    public function get entityDp():Object
    {
        return this._2102100017entityDp;
    }

    public function set entityDp(value:Object):void
    {
    	var oldValue:Object = this._2102100017entityDp;
        if (oldValue !== value)
        {
			this._2102100017entityDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property arithmeticLabel (private)
	 * - generated setter
	 * - generated getter
	 * - original private var 'arithmeticLabel' moved to '_1645490282arithmeticLabel'
	 */

    [Bindable(event="propertyChange")]
    private function get arithmeticLabel():String
    {
        return this._1645490282arithmeticLabel;
    }

    private function set arithmeticLabel(value:String):void
    {
    	var oldValue:Object = this._1645490282arithmeticLabel;
        if (oldValue !== value)
        {
			this._1645490282arithmeticLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "arithmeticLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property relationalLabel (private)
	 * - generated setter
	 * - generated getter
	 * - original private var 'relationalLabel' moved to '_132020237relationalLabel'
	 */

    [Bindable(event="propertyChange")]
    private function get relationalLabel():String
    {
        return this._132020237relationalLabel;
    }

    private function set relationalLabel(value:String):void
    {
    	var oldValue:Object = this._132020237relationalLabel;
        if (oldValue !== value)
        {
			this._132020237relationalLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "relationalLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeValueLabel (private)
	 * - generated setter
	 * - generated getter
	 * - original private var 'timeValueLabel' moved to '_547557040timeValueLabel'
	 */

    [Bindable(event="propertyChange")]
    private function get timeValueLabel():String
    {
        return this._547557040timeValueLabel;
    }

    private function set timeValueLabel(value:String):void
    {
    	var oldValue:Object = this._547557040timeValueLabel;
        if (oldValue !== value)
        {
			this._547557040timeValueLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeValueLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property timeIntrvalLabel (private)
	 * - generated setter
	 * - generated getter
	 * - original private var 'timeIntrvalLabel' moved to '_476822749timeIntrvalLabel'
	 */

    [Bindable(event="propertyChange")]
    private function get timeIntrvalLabel():String
    {
        return this._476822749timeIntrvalLabel;
    }

    private function set timeIntrvalLabel(value:String):void
    {
    	var oldValue:Object = this._476822749timeIntrvalLabel;
        if (oldValue !== value)
        {
			this._476822749timeIntrvalLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "timeIntrvalLabel", oldValue, value));
        }
    }


}
