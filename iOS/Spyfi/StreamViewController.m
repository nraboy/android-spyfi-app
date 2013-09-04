// Spyfi
// Created by Nic Raboy

#import "StreamViewController.h"

@interface StreamViewController ()

@end

@implementation StreamViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _streamUrl = [NSString stringWithFormat:@"http://%@:%@/snapshot.cgi?user=%@&pwd=%@", [[self.cameraItem valueForKey:@"host"] description], [[self.cameraItem valueForKey:@"port"] description], [[self.cameraItem valueForKey:@"username"] description], [[self.cameraItem valueForKey:@"password"] description]];
    _cameraImage.image = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:_streamUrl]]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (IBAction)navigationBack:(id)sender {
    [self dismissViewControllerAnimated:NO completion:nil];
}

@end
