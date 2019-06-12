// CroneEngine_NES2
// NES2

// Emulation of the sound generation hardware of the NES APU chip by Matthew Conte. 
// This UGen has 5 oscillators: 2 squares, 1 triangle, 1 noise, 1 dmc.
// References:
// http://nesdev.com/apu_ref.txt
// http://web.textfiles.com/games/nessound.txt
// http://nesdev.com/dmc.txt

//

Engine_NES2 : CroneEngine {
	var pg;
    var amp=0.2;
    var attack=0.1;
    var sustain=0.5;
    var release=0.5;

// a = 2 square waves
    var a0=0; // ## bits 7-6 || duty cycle, ## bit 5 || loop envelope, ## bit 4 || envelope decay disable, ## bits 3-0 || volume / envelope decay rate (4bits 0-15), 
    var a1=0; // ## bit 7 || sweep on, ## bits 6-4 || sweep length, ## bit 3 || sweep inc/dec, ## bits 2-0 || sweep shifts (3bits 0-7), 
    var a2=0; // ## bits 7-0 || frequency low bits (8bits 0-255)
    var a3=0; // ## bits 7-3 || vbl length counter, ## bits 2-0 || frequency high bits (3bits 0-7), 

// b = 2 square waves
    var b0=0; // ## bits 7-6 || duty cycle, ## bit 5 || loop envelope, ## bit 4 || envelope decay disable, ## bits 3-0 || volume / envelope decay rate (4bits 0-15), 
    var b1=0; // ## bit 7 || sweep on, ## bits 6-4 || sweep length, ## bit 3 || sweep inc/dec, ## bits 2-0 || sweep shifts (3bits 0-7), 
    var b2=0; // ## bits 7-0 || frequency low bits (8bits 0-255)
    var b3=0; // ## bits 7-3 || vbl length counter, ## bits 2-0 || frequency high bits (3bits 0-7)

// c = triangle
    var c0=0; // ## bit 7 || linear counter start, ## bits 6-0 || linear counter (7bits 0-127)
    var c2=0; // ## bits 7-0 || frequency low bits (8bits 0-255)
    var c3=0; // ## bits 7-3 || length counter, ## bits 2-0 || frequency high bits (3bits 0-7)

// d = noise
    var d0=0; // ## bit 5 || loop envelope, ## bit 4 || envelope decay disable, ## bits 3-0 || volume / envelope decay rate (4bits 0-15), 
    var d2=0; // ## bit 7 || short mode, ## bits 3-0 || playback sample rate (4bits 0-15), 
    var d3=0; // ## bits 7-3 || length counter
    
// e = delta modulation channel (dmu)
    var e0=0; // ## bit 7 || irq generator strong::(not in use)::, ## bit 6 || looping, ## bits 3-0 || frequency control (4bits 0-15), 
    var e1=0; // ## bits 6-0 || delta count register or output dc level strong::(not in use)::
    var e2=0; // ## bits 7-0 || address load register strong::(not in use)::
    var e3=0; // ## bits 7-0 || length register strong::(not in use)::

    var smask=0; // ## bit 4 || dmc channel enabled, ## bit 3 || noise channel enabled, ## bit 2 || triangle wave channel enabled, ## bit 1 || square wave channel 2 enabled, ## bit 0 || square wave channel 1 enabled, 

	var rate=1;
	
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		pg = ParGroup.tail(context.xg);

		SynthDef("NES2", {
			arg out, a0=a0, a1=a1, a2=a2, a3=a3, b0=b0, b1=b1, b2=b2, b3=b3, c0=c0, c2=c2, c3=c3, d0=d0, d2=d2, d3=d3, e0=e0, e1=e1, e2=e2, e3=e3, smask=smask, amp=1, pan=0;
			var e, z;
			e = Env.linen(attackTime: attack, sustainTime: sustain, releaseTime: release, level: amp).kr(2);
			z = NES2.ar(a0, a1, a2, a3, b0, b1, b2, b3, c0, c2, c3, d0, d2, d3, e0, e1, e2, e3, smask);
			Out.ar(out, Pan2.ar(z*e, pan));
		}).add;


		this.addCommand("trig", "", {
        	Synth("NES2", [\out, context.out_b,
        	\a0,a0,\a1,a1,\a2,a2,\a3,a3,
        	\b0,b0,\b1,b1,\b2,b2,\b3,b3,
        	\c0,c0,\c2,c2,\c3,c3,
        	\d0,d0,\d2,d2,\d3,d3,
        	\e0,e0,\e1,e1,\e2,e2,\e3,e3,\smask,smask
        	], target:pg);
		});

		this.addCommand("amp", "f", { arg msg;
			amp = msg[1];
		});
		this.addCommand("a0", "f", { arg msg;
			a0 = msg[1];
		});
		this.addCommand("a1", "f", { arg msg;
			a1 = msg[1];
		});
		this.addCommand("a2", "f", { arg msg;
			a2 = msg[1];
		});
		this.addCommand("a3", "f", { arg msg;
			a3 = msg[1];
		});
		this.addCommand("b0", "f", { arg msg;
			b0 = msg[1];
		});
		this.addCommand("b1", "f", { arg msg;
			b1 = msg[1];
		});
		this.addCommand("b2", "f", { arg msg;
			b2 = msg[1];
		});
		this.addCommand("b3", "f", { arg msg;
			b3 = msg[1];
		});
		this.addCommand("c0", "f", { arg msg;
			c0 = msg[1];
		});
		this.addCommand("c2", "f", { arg msg;
			c2 = msg[1];
		});
		this.addCommand("c3", "f", { arg msg;
			c3 = msg[1];
		});
		this.addCommand("d0", "f", { arg msg;
			d0 = msg[1];
		});
		this.addCommand("d2", "f", { arg msg;
			d2 = msg[1];
		});
		this.addCommand("d3", "f", { arg msg;
			d3 = msg[1];
		});
		this.addCommand("e0", "f", { arg msg;
			e0 = msg[1];
		});
		this.addCommand("e1", "f", { arg msg;
			e1 = msg[1];
		});
		this.addCommand("e2", "f", { arg msg;
			e2 = msg[1];
		});
		this.addCommand("e3", "f", { arg msg;
			e3 = msg[1];
		});
		this.addCommand("smask", "f", { arg msg;
			smask = msg[1];
		});

		this.addCommand("attack", "f", { arg msg;
			attack = msg[1];
		});
		this.addCommand("sustain", "f", { arg msg;
			sustain = msg[1];
		});
		this.addCommand("release", "f", { arg msg;
			release = msg[1];
		});

	}
}
