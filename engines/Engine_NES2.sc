// CroneEngine_NES2y
// NES2

// Emulation of the sound generation hardware of the NES APU chip by Matthew Conte. 
// This UGen has 5 oscillators: 2 squares, 1 triangle, 1 noise, 1 dmc.
// References:
// http://nesdev.com/apu_ref.txt
// http://web.textfiles.com/games/nessound.txt
// http://nesdev.com/dmc.txt

/*
trig		Control or audio rate trigger.
dutycycle	Type (0-3).
loopenv		Loop envelope off or on (0/1).
envdecay	Envelope decay off or on (0/1).
vol			Volume (0-15).
sweep		Off or on (0/1).
sweeplen	Sweeplength (0-7).
sweepdir	Sweepdirection decrease or increase (0/1).
sweepshi	Sweepshift (0-7).
freq		Frequency (0-2047).
vbl	 		Length counter (0-31).
*/

Engine_NES2y : CroneEngine {
	// Nes2Square.ar(trig: 0, dutycycle: 0, loopenv: 0, envdecay: 0, vol: 10, sweep: 0, sweeplen: 0, sweepdir: 0, sweepshi: 0, freq: 100, vbl: 0)
	
	var <synth;
		
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		// Define the synth variable, whichis a function
		synth = {
			// define arguments to the function

			arg out, gate=0, dutycycle=0, loopenv=0, envdecay=0, vol=10, sweep=0, sweeplen=0, sweepdir=0, sweepshi=0, freq=100, vbl=0, pan=0, amp=1 ;
			var z;
			z = Nes2Square.ar(gate, dutycycle, loopenv, envdecay, vol, sweep, sweeplen, sweepdir, sweepshi, freq, vbl);
			Out.ar(out, Pan2.ar(z, pan));
			
		}.play(args: [\out, context.out_b], target: context.xg);

			
		// noteOn(freq)
		this.addCommand("noteOn", "f", { arg msg;
			synth.set(\freq, msg[1], \gate, 1);
		});

		this.addCommand("noteOff", "", { arg msg;
			synth.set(\gate, 0);
		});


		this.addCommand("dutycycle", "f", { arg msg;
			synth.set(\dutycycle, msg[1]);
		});
		this.addCommand("loopenv", "f", { arg msg;
			synth.set(\loopenv, msg[1]);
		});
		this.addCommand("envdecay", "f", { arg msg;
			synth.set(\envdecay, msg[1]);
		});
		this.addCommand("vol", "f", { arg msg;
			synth.set(\vol, msg[1]);
		});
		this.addCommand("sweep", "f", { arg msg;
			synth.set(\sweeplen, msg[1]);
		});
		this.addCommand("sweeplen", "f", { arg msg;
			synth.set(\sweeplen, msg[1]);
		});
		this.addCommand("sweepdir", "f", { arg msg;
			synth.set(\sweepdir, msg[1]);
		});
		this.addCommand("sweepshi", "f", { arg msg;
			synth.set(\sweepshi, msg[1]);
		});
//		this.addCommand("freq", "f", { arg msg;
//			synth.set(\freq, msg[1]);
//		});
		this.addCommand("vbl", "i", { arg msg;
			synth.set(\vbl, msg[1]);
		});
	}
	// define a function that is called when the synth is shut down
	free {
		synth.free;
	}
}
