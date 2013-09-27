
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
import mx.containers.VBox;
import mx.controls.CheckBox;
import int;
import Object;
import mx.controls.Label;
import mx.controls.ComboBox;

class BindableProperty
{
	/**
	 * generated bindable wrapper for property ClassName1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'ClassName1' moved to '_1686914190ClassName1'
	 */

    [Bindable(event="propertyChange")]
    public function get ClassName1():mx.controls.Label
    {
        return this._1686914190ClassName1;
    }

    public function set ClassName1(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._1686914190ClassName1;
        if (oldValue !== value)
        {
			this._1686914190ClassName1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "ClassName1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property ClassName2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'ClassName2' moved to '_1686914191ClassName2'
	 */

    [Bindable(event="propertyChange")]
    public function get ClassName2():mx.controls.Label
    {
        return this._1686914191ClassName2;
    }

    public function set ClassName2(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._1686914191ClassName2;
        if (oldValue !== value)
        {
			this._1686914191ClassName2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "ClassName2", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property addAttributeId (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'addAttributeId' moved to '_311274870addAttributeId'
	 */

    [Bindable(event="propertyChange")]
    public function get addAttributeId():mx.containers.VBox
    {
        return this._311274870addAttributeId;
    }

    public function set addAttributeId(value:mx.containers.VBox):void
    {
    	var oldValue:Object = this._311274870addAttributeId;
        if (oldValue !== value)
        {
			this._311274870addAttributeId = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "addAttributeId", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property deleteAllChkBox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'deleteAllChkBox' moved to '_118640933deleteAllChkBox'
	 */

    [Bindable(event="propertyChange")]
    public function get deleteAllChkBox():mx.controls.CheckBox
    {
        return this._118640933deleteAllChkBox;
    }

    public function set deleteAllChkBox(value:mx.controls.CheckBox):void
    {
    	var oldValue:Object = this._118640933deleteAllChkBox;
        if (oldValue !== value)
        {
			this._118640933deleteAllChkBox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "deleteAllChkBox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property deleteAllLabel (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'deleteAllLabel' moved to '_272826210deleteAllLabel'
	 */

    [Bindable(event="propertyChange")]
    public function get deleteAllLabel():mx.controls.Label
    {
        return this._272826210deleteAllLabel;
    }

    public function set deleteAllLabel(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._272826210deleteAllLabel;
        if (oldValue !== value)
        {
			this._272826210deleteAllLabel = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "deleteAllLabel", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property deleteButton (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'deleteButton' moved to '_1245745987deleteButton'
	 */

    [Bindable(event="propertyChange")]
    public function get deleteButton():mx.controls.Button
    {
        return this._1245745987deleteButton;
    }

    public function set deleteButton(value:mx.controls.Button):void
    {
    	var oldValue:Object = this._1245745987deleteButton;
        if (oldValue !== value)
        {
			this._1245745987deleteButton = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "deleteButton", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityLabel1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityLabel1' moved to '_516127104entityLabel1'
	 */

    [Bindable(event="propertyChange")]
    public function get entityLabel1():mx.controls.Label
    {
        return this._516127104entityLabel1;
    }

    public function set entityLabel1(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._516127104entityLabel1;
        if (oldValue !== value)
        {
			this._516127104entityLabel1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityLabel1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property entityLabel2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'entityLabel2' moved to '_516127105entityLabel2'
	 */

    [Bindable(event="propertyChange")]
    public function get entityLabel2():mx.controls.Label
    {
        return this._516127105entityLabel2;
    }

    public function set entityLabel2(value:mx.controls.Label):void
    {
    	var oldValue:Object = this._516127105entityLabel2;
        if (oldValue !== value)
        {
			this._516127105entityLabel2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "entityLabel2", oldValue, value));
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
	 * generated bindable wrapper for property leftDropDown (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'leftDropDown' moved to '_1803339096leftDropDown'
	 */

    [Bindable(event="propertyChange")]
    public function get leftDropDown():ArrayCollection
    {
        return this._1803339096leftDropDown;
    }

    public function set leftDropDown(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1803339096leftDropDown;
        if (oldValue !== value)
        {
			this._1803339096leftDropDown = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "leftDropDown", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property rightDropDown (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'rightDropDown' moved to '_1659043219rightDropDown'
	 */

    [Bindable(event="propertyChange")]
    public function get rightDropDown():ArrayCollection
    {
        return this._1659043219rightDropDown;
    }

    public function set rightDropDown(value:ArrayCollection):void
    {
    	var oldValue:Object = this._1659043219rightDropDown;
        if (oldValue !== value)
        {
			this._1659043219rightDropDown = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "rightDropDown", oldValue, value));
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
	 * generated bindable wrapper for property counter (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'counter' moved to '_957830652counter'
	 */

    [Bindable(event="propertyChange")]
    public function get counter():int
    {
        return this._957830652counter;
    }

    public function set counter(value:int):void
    {
    	var oldValue:Object = this._957830652counter;
        if (oldValue !== value)
        {
			this._957830652counter = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "counter", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property attributes (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'attributes' moved to '_405645655attributes'
	 */

    [Bindable(event="propertyChange")]
    public function get attributes():int
    {
        return this._405645655attributes;
    }

    public function set attributes(value:int):void
    {
    	var oldValue:Object = this._405645655attributes;
        if (oldValue !== value)
        {
			this._405645655attributes = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "attributes", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property hbox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'hbox' moved to '_3196003hbox'
	 */

    [Bindable(event="propertyChange")]
    public function get hbox():HBox
    {
        return this._3196003hbox;
    }

    public function set hbox(value:HBox):void
    {
    	var oldValue:Object = this._3196003hbox;
        if (oldValue !== value)
        {
			this._3196003hbox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "hbox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property vbox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'vbox' moved to '_3613077vbox'
	 */

    [Bindable(event="propertyChange")]
    public function get vbox():VBox
    {
        return this._3613077vbox;
    }

    public function set vbox(value:VBox):void
    {
    	var oldValue:Object = this._3613077vbox;
        if (oldValue !== value)
        {
			this._3613077vbox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "vbox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property vbox1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'vbox1' moved to '_112005436vbox1'
	 */

    [Bindable(event="propertyChange")]
    public function get vbox1():VBox
    {
        return this._112005436vbox1;
    }

    public function set vbox1(value:VBox):void
    {
    	var oldValue:Object = this._112005436vbox1;
        if (oldValue !== value)
        {
			this._112005436vbox1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "vbox1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property vbox2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'vbox2' moved to '_112005437vbox2'
	 */

    [Bindable(event="propertyChange")]
    public function get vbox2():VBox
    {
        return this._112005437vbox2;
    }

    public function set vbox2(value:VBox):void
    {
    	var oldValue:Object = this._112005437vbox2;
        if (oldValue !== value)
        {
			this._112005437vbox2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "vbox2", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property checkBox (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'checkBox' moved to '_1536861091checkBox'
	 */

    [Bindable(event="propertyChange")]
    public function get checkBox():CheckBox
    {
        return this._1536861091checkBox;
    }

    public function set checkBox(value:CheckBox):void
    {
    	var oldValue:Object = this._1536861091checkBox;
        if (oldValue !== value)
        {
			this._1536861091checkBox = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "checkBox", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property comboBox1 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'comboBox1' moved to '_1802016140comboBox1'
	 */

    [Bindable(event="propertyChange")]
    public function get comboBox1():ComboBox
    {
        return this._1802016140comboBox1;
    }

    public function set comboBox1(value:ComboBox):void
    {
    	var oldValue:Object = this._1802016140comboBox1;
        if (oldValue !== value)
        {
			this._1802016140comboBox1 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "comboBox1", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property comboBox2 (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'comboBox2' moved to '_1802016139comboBox2'
	 */

    [Bindable(event="propertyChange")]
    public function get comboBox2():ComboBox
    {
        return this._1802016139comboBox2;
    }

    public function set comboBox2(value:ComboBox):void
    {
    	var oldValue:Object = this._1802016139comboBox2;
        if (oldValue !== value)
        {
			this._1802016139comboBox2 = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "comboBox2", oldValue, value));
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
