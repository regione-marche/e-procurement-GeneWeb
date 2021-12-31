var newsMarquee = Class.create({

	initialize: function(element,options) {		
		this.element = $(element);
		this.innerDiv = this.element.down('div');
		this.options = {
			speed: 3, 
			control: true 
		};
		Object.extend(this.options, options || {});
		
		this.playScroll();
		if (this.options.control) {
			this.addObserver();
		}
	},
	
	addObserver: function() {
		this.element.observe('mouseover', this.pauseScroll.bind(this));
		this.element.observe('mouseout', this.playScroll.bind(this));	
	},
	
	playScroll: function(){
  		this.scrolling = true;
  		this.startScroll();
	},
	
	pauseScroll: function(){
		if (this.timeout) {	
			this.timeout.stop();
			this.scrolling = false;
		}
	},
	
	startScroll: function(){ 		
		if (this.scrolling) {   		
			this.timeout = new PeriodicalExecuter(function(){
	  			this.executeScroll();
	 		}.bind(this), this.options.speed/100);
		}		
	}
		
});

var horizontalMarquee = Class.create(newsMarquee,{

	initialize: function($super,element,options) {	
		$super(element,options);

		this.initialWidth = this.element.getWidth();		
		this.childWidth = 0;		
		this.childs = this.innerDiv.childElements();
		this.childs[0].style.paddingLeft= this.initialWidth+'px';
		this.childs[this.childs.length-1].style.paddingRight= this.initialWidth+'px';
		this.childs.each(function(node) {			
 				this.childWidth += node.getWidth();
 			}.bind(this)
 		)		
  		this.innerDiv.style.width = this.childWidth+'px';
	}, 
	
	executeScroll: function() {
		if (this.element.scrollLeft > (this.element.scrollWidth-this.initialWidth)) {
    		this.element.scrollLeft = 0;
  		}
		this.element.scrollLeft = this.element.scrollLeft+1;		
	}

});


var verticalMarquee = Class.create(newsMarquee,{

	initialize: function($super,element,options) {
		$super(element,options);		
		this.initialHeight = this.element.getHeight();
		this.innerDiv.style.paddingTop = this.initialHeight+'px';
  		this.innerDiv.style.paddingBottom = this.initialHeight+'px';	
	}, 
	
	executeScroll: function() {
		if (this.element.scrollTop>=(this.element.scrollHeight-this.initialHeight)) {
			this.element.scrollTop=0; 	
		}
		this.element.scrollTop = this.element.scrollTop+1;	
	}

});
