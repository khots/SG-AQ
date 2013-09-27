package Components
{
import flash.accessibility.*;
import flash.debugger.*;
import flash.display.*;
import flash.errors.*;
import flash.events.*;
import flash.external.*;
import flash.filters.*;
import flash.geom.*;
import flash.media.*;
import flash.net.*;
import flash.printing.*;
import flash.profiler.*;
import flash.system.*;
import flash.text.*;
import flash.ui.*;
import flash.utils.*;
import flash.utils.IExternalizable;
import flash.xml.*;
import mx.binding.*;
import mx.containers.Box;
import mx.containers.HBox;
import mx.controls.Label;
import mx.core.ClassFactory;
import mx.core.DeferredInstanceFromClass;
import mx.core.DeferredInstanceFromFunction;
import mx.core.IDeferredInstance;
import mx.core.IFactory;
import mx.core.IPropertyChangeNotifier;
import mx.core.mx_internal;
import mx.styles.*;
import mx.controls.Button;
import mx.containers.VBox;
import mx.containers.Box;

public class SingleNodeCustomFormulaNode extends mx.containers.Box
 implements flash.utils.IExternalizable {
	public function SingleNodeCustomFormulaNode() {}

	[Bindable]
	public var sNodeFormula : mx.containers.HBox;
	[Bindable]
	public var formulaLabel : mx.controls.Label;

	public var _bindingsByDestination : Object;
	public var _bindingsBeginWithWord : Object;

include "E:/workplace/AdvancedQueryForJoin_NewCP/flexclient/advQuery/dag/Components/SingleNodeCustomFormulaNode.mxml:6,358";

}}
