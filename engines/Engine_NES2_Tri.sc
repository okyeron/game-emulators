// CroneEngine_NES2
// NES2

// Emulation of the sound generation hardware of the NES APU chip by Matthew Conte. 
// This UGen has 5 oscillators: 2 squares, 1 triangle, 1 noise, 1 dmc.
// References:
// http://nesdev.com/apu_ref.txt
// http://web.textfiles.com/games/nessound.txt
// http://nesdev.com/dmc.txt

/*
trig	
Control or audio rate trigger.

start	
Linear counter start (0/1).

counter	
Linear counter (0-127).

freq	
Frequency (0-2047).

vbl	
Length counter (0-31).
*/

Engine_NES2_Tri : CroneEngine {
	// Nes2Triangle.ar(trig: 0, start: 0, counter: 10, freq: 100, vbl: 0)
		
	var pg;
	var trig_a;
	var trig_b;
	var trig_c;
	var trig;
	var gate = 0;
	var amp = 0.8;
	var pan = 0;
	var start = 0;
	var counter = 0;
	var freq = 100;
	var length = 0;

	var attack = 0.0001;
	var decay = 8.0;
	var sustain = 0;
	var release = 0.01;

	var synthTRI;
	
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		pg = ParGroup.tail(context.xg);
		
		SynthDef("synthTRI", {
			arg out, start=start, counter=counter, freq=freq, length=length, pan=pan, gate=gate, attack=attack, decay=decay, sustain=sustain, release=release ;
			var ampEnv, z ;
//			ampEnv = EnvGen.kr(
//				Env.adsr(attackTime: attack,decayTime: decay,sustainLevel: sustain,releaseTime: release,
//				curve: -4.0
//			), gate, doneAction: 0);

			z = Nes2Triangle.ar(gate, start, counter, freq, length);

//			Out.ar(out, Pan2.ar(z*ampEnv, pan));

			Out.ar(out, Pan2.ar(z*amp, pan));
		}).add;
		
		Server.default.sync;
		synthTRI = Synth("synthTRI", target:pg);
		synthTRI.set(
			\gate, gate,
			\attack, attack,
			\decay, decay,
			\sustain, sustain,
			\release, release,

			\pan, pan,
			\amp, amp,
			\start, start,
			\counter, counter,
			\freq, freq,
			\length, length
		);
		this.addCommand("gate", "f", { arg msg;
			gate = msg[1];
			synthTRI.set(\gate, gate);
		});
		this.addCommand("start", "f", { arg msg;
			start = msg[1];
			synthTRI.set(\start, start);
		});
		this.addCommand("counter", "f", { arg msg;
			counter = msg[1];
			synthTRI.set(\counter,counter);
		});
		this.addCommand("freq", "f", { arg msg;
			freq = msg[1];
			synthTRI.set(\freq, freq);
		});
		this.addCommand("length", "i", { arg msg;
			length = msg[1];
			synthTRI.set(\length,length);
		});
		this.addCommand("pan", "f", { arg msg;
			pan = msg[1];
			synthTRI.set(\pan, pan);
		});
		this.addCommand("amp", "f", { arg msg;
			amp = msg[1];
			synthTRI.set(\amp, amp);
		});
		this.addCommand("attack", "f", { arg msg;
			attack = msg[1];
			synthTRI.set(\attack, attack);
		});
		this.addCommand("release", "f", { arg msg;
			release = msg[1];
			synthTRI.set(\release, release);
		});
		this.addCommand("decay", "f", { arg msg;
			decay = msg[1];
			synthTRI.set(\decay, decay);
		});
		this.addCommand("sustain", "f", { arg msg;
			sustain = msg[1];
			synthTRI.set(\sustain, sustain);
		});

	} // end alloc

	free {
		synthTRI.free;
	}

} // end class