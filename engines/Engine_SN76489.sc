// CroneEngine_SN76489
// SN76489
// Revised August 2023 - steven noreyko

// http://www.smspower.org/Development/SN76489

// 3 tone channels - tone0, tone1, tone2 (10bits 0-1023)
// 1 noise channels - noise (3bits 0-7)
// volume for each channel (4bits 0-15)

Engine_SN76489 : CroneEngine {
	var pg;
    var amp=0.5;
    var attack=0.1;
    var sustain=0.5;
    var release=0.5;
    var tone0=1000; // (10bits 0-1023)
    var tone1=500; // (10bits 0-1023)
    var tone2=750; // (10bits 0-1023)
    var noise=0; // (3bits 0-7)
    var vol0=15; // (4bits 0-15)
    var vol1=15; // (4bits 0-15)
    var vol2=15; // (4bits 0-15)
    var vol3=15; // (4bits 0-15)
	var rate=1;
	var pan=0;

	// Synth instance
	var sn76489;
	
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		pg = ParGroup.tail(context.xg);

		SynthDef("SN76489", {
			arg out, tone0=tone0, tone1=tone1, tone2=tone2, noise=noise, vol0=vol0, vol1=vol1, vol2=vol2, vol3=vol3, amp=amp, attack=attack, sustain=sustain, release=release, rate=1, pan=1 ;
			var e, z;
//			e = Env.linen(attackTime: attack, sustainTime: sustain, releaseTime: release, level: amp).kr(2);
			z = SN76489.ar(tone0, tone1, tone2, noise, vol0, vol1, vol2, vol3, rate);
			Out.ar(out, Pan2.ar(z*amp, pan));
		}).add;

		// https://llllllll.co/t/supercollider-engine-failure-in-server-error/53051
		Server.default.sync;

		sn76489 = Synth("SN76489", target:pg);
		sn76489.set(
			\pan, pan,
			\amp, amp,
			\tone0, tone0,
			\tone1, tone1,
			\tone2, tone2,
			\noise, noise,
			\vol0, vol0,
			\vol1, vol1,
			\vol2, vol2,
			\vol3, vol3,

			\rate, rate
		);


//		this.addCommand("trig", "", {
//        	Synth("SN76489", [\out, context.out_b,\amp,amp,\tone0,tone0,\tone1,tone1,\tone2,tone2,\noise,noise,\vol0,vol0,\vol1,vol1,\vol2,vol2,\vol3,vol3,\release,release,\attack,attack,\sustain,sustain], target:pg);
//		});

		this.addCommand("pan", "f", { arg msg;
			pan = msg[1];
			sn76489.set(\pan, pan);
		});
		this.addCommand("amp", "f", { arg msg;
			amp = msg[1];
			sn76489.set(\amp, amp);
		});
		this.addCommand("tone0", "f", { arg msg;
			tone0 = msg[1];
			sn76489.set(\tone0, tone0);
		});
		this.addCommand("tone1", "f", { arg msg;
			tone1 = msg[1];
			sn76489.set(\tone1, tone1);
		});
		this.addCommand("tone2", "f", { arg msg;
			tone2 = msg[1];
			sn76489.set(\tone2, tone2);
		});
		this.addCommand("noise", "f", { arg msg;
			noise = msg[1];
			sn76489.set(\noise, noise);
		});
		this.addCommand("vol0", "f", { arg msg;
			vol0 = msg[1];
			sn76489.set(\vol0, vol0);
		});
		this.addCommand("vol1", "f", { arg msg;
			vol1 = msg[1];
			sn76489.set(\vol1, vol1);
		});
		this.addCommand("vol2", "f", { arg msg;
			vol2 = msg[1];
			sn76489.set(\vol2, vol2);
		});
		this.addCommand("vol3", "f", { arg msg;
			vol3 = msg[1];
			sn76489.set(\vol3, vol3);
		});
		this.addCommand("rate", "f", { arg msg;
			rate = msg[1];
			sn76489.set(\rate, rate);
		});
//		this.addCommand("attack", "f", { arg msg;
//			attack = msg[1];
//		});
//		this.addCommand("sustain", "f", { arg msg;
//			sustain = msg[1];
//		});
//		this.addCommand("release", "f", { arg msg;
//			release = msg[1];
//		});

	}
	free {
		sn76489.free;
	}
	
}