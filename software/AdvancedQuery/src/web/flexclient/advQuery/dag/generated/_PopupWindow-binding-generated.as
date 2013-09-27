
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
import mx.collections.ArrayCollection;
import mx.formatters.DateFormatter;
import mx.containers.VBox;
import mx.controls.TextInput;
import mx.validators.StringValidator;
import int;
import Object;
import mx.controls.Label;
import mx.controls.ComboBox;

class BindableProperty
{
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
	 * generated bindable wrapper for property cb1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb1' moved to '_98226cb1'
	 */

    [Bindable(event="propertyChange")]
    public function get cb1():mx.controls.ComboBox
    {
        return this._98226cb1;
    }

    public function set cb1(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._98226cb1;
        if (oldValue !== value)
        {
			this._98226cb1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property cb2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb2' moved to '_98227cb2'
	 */

    [Bindable(event="propertyChange")]
    public function get cb2():mx.controls.ComboBox
    {
        return this._98227cb2;
    }

    public function set cb2(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._98227cb2;
        if (oldValue !== value)
        {
			this._98227cb2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb2", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property cb3 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb3' moved to '_98228cb3'
	 */

    [Bindable(event="propertyChange")]
    public function get cb3():mx.controls.ComboBox
    {
        return this._98228cb3;
    }

    public function set cb3(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._98228cb3;
        if (oldValue !== value)
        {
			this._98228cb3 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb3", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property cb4 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb4' moved to '_98229cb4'
	 */

    [Bindable(event="propertyChange")]
    public function get cb4():mx.controls.ComboBox
    {
        return this._98229cb4;
    }

    public function set cb4(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._98229cb4;
        if (oldValue !== value)
        {
			this._98229cb4 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb4", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property cb5 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb5' moved to '_98230cb5'
	 */

    [Bindable(event="propertyChange")]
    public function get cb5():mx.controls.ComboBox
    {
        return this._98230cb5;
    }

    public function set cb5(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._98230cb5;
        if (oldValue !== value)
        {
			this._98230cb5 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb5", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property cb6 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb6' moved to '_98231cb6'
	 */

    [Bindable(event="propertyChange")]
    public function get cb6():mx.controls.ComboBox
    {
        return this._98231cb6;
    }

    public function set cb6(value:mx.controls.ComboBox):void
    {
    	var oldValue:Object = this._98231cb6;
        if (oldValue !== value)
        {
			this._98231cb6 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb6", oldValue, value));
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
	 * generated bindable wrapper for property lb1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb1' moved to '_106875lb1'
	 */

    [Bindable(event="propertyChange")]
    public function get lb1():mx.controls.Label
    {
        return this._106875lb1;
    }

    public function set lb1(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106875lb1;
        if (oldValue !== value)
        {
			this._106875lb1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property lb2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb2' moved to '_106876lb2'
	 */

    [Bindable(event="propertyChange")]
    public function get lb2():mx.controls.Label
    {
        return this._106876lb2;
    }

    public function set lb2(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106876lb2;
        if (oldValue !== value)
        {
			this._106876lb2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb2", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property lb3 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb3' moved to '_106877lb3'
	 */

    [Bindable(event="propertyChange")]
    public function get lb3():mx.controls.Label
    {
        return this._106877lb3;
    }

    public function set lb3(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106877lb3;
        if (oldValue !== value)
        {
			this._106877lb3 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb3", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property lb4 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'lb4' moved to '_106878lb4'
	 */

    [Bindable(event="propertyChange")]
    public function get lb4():mx.controls.Label
    {
        return this._106878lb4;
    }

    public function set lb4(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._106878lb4;
        if (oldValue !== value)
        {
			this._106878lb4 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "lb4", oldValue, value));
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
	 * generated bindable wrapper for property secondComboPlace (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'secondComboPlace' moved to '_2034409389secondComboPlace'
	 */

    [Bindable(event="propertyChange")]
    public function get secondComboPlace():mx.containers.VBox
    {
        return this._2034409389secondComboPlace;
    }

    public function set secondComboPlace(value:mx.containers.VBox):void
    {
    	var oldValue:Object = this._2034409389secondComboPlace;
        if (oldValue !== value)
        {
			this._2034409389secondComboPlace = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "secondComboPlace", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property submitButton (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'submitButton' moved to '_831054186submitButton'
	 */

    [Bindable(event="propertyChange")]
    public function get submitButton():mx.controls.Button
    {
        return this._831054186submitButton;
    }

    public function set submitButton(value:mx.controls.Button):void
    {
    	var oldValue:Object = this._831054186submitButton;
        if (oldValue !== value)
        {
			this._831054186submitButton = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "submitButton", oldValue, value));
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
	 * generated bindable wrapper for property txtInput (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'txtInput' moved to '_1474385094txtInput'
	 */

    [Bindable(event="propertyChange")]
    public function get txtInput():mx.controls.TextInput
    {
        return this._1474385094txtInput;
    }

    public function set txtInput(value:mx.controls.TextInput):void
    {
    	var oldValue:Object = this._1474385094txtInput;
        if (oldValue !== value)
        {
			this._1474385094txtInput = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "txtInput", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myArray1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myArray1' moved to '_1323769404myArray1'
	 */

    [Bindable(event="propertyChange")]
    public function get myArray1():ArrayCollection
    {
        return this._1323769404myArray1;
    }

    public function set myArray1(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1323769404myArray1;
        if (oldValue !== value)
        {
			this._1323769404myArray1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myArray1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myArray2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myArray2' moved to '_1323769403myArray2'
	 */

    [Bindable(event="propertyChange")]
    public function get myArray2():ArrayCollection
    {
        return this._1323769403myArray2;
    }

    public function set myArray2(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1323769403myArray2;
        if (oldValue !== value)
        {
			this._1323769403myArray2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myArray2", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myArray3 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myArray3' moved to '_1323769402myArray3'
	 */

    [Bindable(event="propertyChange")]
    public function get myArray3():ArrayCollection
    {
        return this._1323769402myArray3;
    }

    public function set myArray3(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1323769402myArray3;
        if (oldValue !== value)
        {
			this._1323769402myArray3 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myArray3", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myArray4 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myArray4' moved to '_1323769401myArray4'
	 */

    [Bindable(event="propertyChange")]
    public function get myArray4():ArrayCollection
    {
        return this._1323769401myArray4;
    }

    public function set myArray4(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1323769401myArray4;
        if (oldValue !== value)
        {
			this._1323769401myArray4 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myArray4", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property myArray5 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'myArray5' moved to '_1323769400myArray5'
	 */

    [Bindable(event="propertyChange")]
    public function get myArray5():ArrayCollection
    {
        return this._1323769400myArray5;
    }

    public function set myArray5(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1323769400myArray5;
        if (oldValue !== value)
        {
			this._1323769400myArray5 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "myArray5", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property dataProvider (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'dataProvider' moved to '_339742651dataProvider'
	 */

    [Bindable(event="propertyChange")]
    public function get dataProvider():Object
    {
        return this._339742651dataProvider;
    }

    public function set dataProvider(value:Object):void
    {
    	var oldValue:Object = this._339742651dataProvider;
        if (oldValue !== value)
        {
			this._339742651dataProvider = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "dataProvider", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property firstNodeDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'firstNodeDp' moved to '_1181271810firstNodeDp'
	 */

    [Bindable(event="propertyChange")]
    public function get firstNodeDp():Object
    {
        return this._1181271810firstNodeDp;
    }

    public function set firstNodeDp(value:Object):void
    {
    	var oldValue:Object = this._1181271810firstNodeDp;
        if (oldValue !== value)
        {
			this._1181271810firstNodeDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "firstNodeDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property secondNodeDp (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'secondNodeDp' moved to '_852473918secondNodeDp'
	 */

    [Bindable(event="propertyChange")]
    public function get secondNodeDp():Object
    {
        return this._852473918secondNodeDp;
    }

    public function set secondNodeDp(value:Object):void
    {
    	var oldValue:Object = this._852473918secondNodeDp;
        if (oldValue !== value)
        {
			this._852473918secondNodeDp = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "secondNodeDp", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property firstEntityName (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'firstEntityName' moved to '_1298842658firstEntityName'
	 */

    [Bindable(event="propertyChange")]
    public function get firstEntityName():String
    {
        return this._1298842658firstEntityName;
    }

    public function set firstEntityName(value:String):void
    {
    	var oldValue:Object = this._1298842658firstEntityName;
        if (oldValue !== value)
        {
			this._1298842658firstEntityName = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "firstEntityName", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property secondEntityName (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'secondEntityName' moved to '_566315170secondEntityName'
	 */

    [Bindable(event="propertyChange")]
    public function get secondEntityName():String
    {
        return this._566315170secondEntityName;
    }

    public function set secondEntityName(value:String):void
    {
    	var oldValue:Object = this._566315170secondEntityName;
        if (oldValue !== value)
        {
			this._566315170secondEntityName = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "secondEntityName", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property firstEntityExpId (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'firstEntityExpId' moved to '_1617041115firstEntityExpId'
	 */

    [Bindable(event="propertyChange")]
    public function get firstEntityExpId():int
    {
        return this._1617041115firstEntityExpId;
    }

    public function set firstEntityExpId(value:int):void
    {
    	var oldValue:Object = this._1617041115firstEntityExpId;
        if (oldValue !== value)
        {
			this._1617041115firstEntityExpId = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "firstEntityExpId", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property secondEntityExpId (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'secondEntityExpId' moved to '_368276705secondEntityExpId'
	 */

    [Bindable(event="propertyChange")]
    public function get secondEntityExpId():int
    {
        return this._368276705secondEntityExpId;
    }

    public function set secondEntityExpId(value:int):void
    {
    	var oldValue:Object = this._368276705secondEntityExpId;
        if (oldValue !== value)
        {
			this._368276705secondEntityExpId = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "secondEntityExpId", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property nodeName (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'nodeName' moved to '_1122880429nodeName'
	 */

    [Bindable(event="propertyChange")]
    public function get nodeName():String
    {
        return this._1122880429nodeName;
    }

    public function set nodeName(value:String):void
    {
    	var oldValue:Object = this._1122880429nodeName;
        if (oldValue !== value)
        {
			this._1122880429nodeName = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "nodeName", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property firstEntityLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'firstEntityLabel' moved to '_1611274239firstEntityLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get firstEntityLabel():String
    {
        return this._1611274239firstEntityLabel;
    }

    public function set firstEntityLabel(value:String):void
    {
    	var oldValue:Object = this._1611274239firstEntityLabel;
        if (oldValue !== value)
        {
			this._1611274239firstEntityLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "firstEntityLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property secondEntityLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'secondEntityLabel' moved to '_374043581secondEntityLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get secondEntityLabel():String
    {
        return this._374043581secondEntityLabel;
    }

    public function set secondEntityLabel(value:String):void
    {
    	var oldValue:Object = this._374043581secondEntityLabel;
        if (oldValue !== value)
        {
			this._374043581secondEntityLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "secondEntityLabel", oldValue, value));
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


}
