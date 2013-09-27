
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import mx.core.IPropertyChangeNotifier;
import mx.events.PropertyChangeEvent;
import mx.utils.ObjectProxy;
import mx.utils.UIDUtil;

import mx.controls.CheckBox;

class BindableProperty
{
	/**
	 * generated bindable wrapper for property cb (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'cb' moved to '_3167cb'
	 */

    [Bindable(event="propertyChange")]
    public function get cb():mx.controls.CheckBox
    {
        return this._3167cb;
    }

    public function set cb(value:mx.controls.CheckBox):void
    {
    	var oldValue:Object = this._3167cb;
        if (oldValue !== value)
        {
			this._3167cb = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "cb", oldValue, value));
        }
    }


}
